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
import variablearc._cocos.CheckNoFieldDependencyCycles4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.CIRCULAR_FIELDS_DEPENDENCY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link CheckNoFieldDependencyCycles4Family}.
 */
class CheckNoFieldDependencyCycles4FamilyTest extends CheckNoFieldDependencyCyclesTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new CheckNoFieldDependencyCycles4Family(new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource({"validModels", "validModelsWithVariability"})
  void shouldNotReportErrorWithVariability(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

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
    checker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  protected static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // fields of a cycle in exclusive variation points
      arg("""
        component ValidCompWithVariability1 {
          feature f;
          varif (f) { int a = b; } else { int b = a; }
          int c = 0;
        }"""),
      // cycle in a dead variation point
      arg("""
        component ValidCompWithVariability2 {
          int a = 0;
          varif (false) { int b = c; int c = b; }
        }""")
    );
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // cycle through a field of a variation point
      arg("""
        component InvalidCompWithVariability1 {
          feature f;
          int a = b;
          varif (f) { int b = a; }
        }""", CIRCULAR_FIELDS_DEPENDENCY)
    );
  }
}
