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

import static montiarc.util.ComfortableArcError.MULTIPLE_AUTOCONNECTS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link MaxOneAutoConnect4Family}.
 */
class MaxOneAutoConnect4FamilyTest extends MaxOneAutoConnectTest {

  protected static MontiArcCoCoChecker familyChecker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new MaxOneAutoConnect4Family(new PresenceResolver(withinComponentResolver)));
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
      // autoconnects in exclusive variation points
      arg("component ValidCompWithVariability1 { feature f; A a; varif (f) { autoconnect port; } else { autoconnect type; } }")
    );
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // autoconnect in a variation point together with an unconditional one
      arg("component InvalidCompWithVariability1 { feature f; A a; autoconnect port; varif (f) { autoconnect type; } }",
        MULTIPLE_AUTOCONNECTS)
    );
  }
}
