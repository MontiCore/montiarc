/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.PrePostError.TRIGGER_MISSING_PORT;
import static montiarc.util.PrePostError.TRIGGER_NOT_AN_IN_PORT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link TriggerPortExists4Family}.
 */
class PrePostTriggerPortExists4FamilyTest extends PrePostTriggerPortExistsTest {

  protected MontiArcCoCoChecker familyChecker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new TriggerPortExists4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModelsWithVariability")
  void shouldNotReportErrorWithVariability(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    familyChecker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource({"invalidModels", "invalidModelsWithVariability"})
  void shouldReportErrorWithVariability(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    familyChecker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  protected static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // trigger port in every configuration
      arg("component ValidCompWithVariability1 { feature f; varif (f) { port in int t; } else { port in boolean t; } trigger: t; post: true; }")
    );
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // trigger port only in some configurations
      arg("component InvalidCompWithVariability1 { feature f; varif (f) { port in int t; } trigger: t; post: true; }",
        TRIGGER_MISSING_PORT),
      // trigger port outgoing in some configurations
      arg("component InvalidCompWithVariability2 { feature f; varif (f) { port in int t; } else { port out int t; } trigger: t; post: true; }",
        TRIGGER_NOT_AN_IN_PORT)
    );
  }
}
