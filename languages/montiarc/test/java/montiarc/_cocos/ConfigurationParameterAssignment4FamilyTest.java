/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ArcBasisASTComponentInstanceCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;
import variablearc._cocos.ConfigurationParameterAssignment4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.TOO_FEW_ARGUMENTS;
import static montiarc.util.ArcError.TOO_MANY_ARGUMENTS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConfigurationParameterAssignment4Family}.
 */
class ConfigurationParameterAssignment4FamilyTest extends ConfigurationParameterAssignmentTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTComponentInstanceCoCo) new ConfigurationParameterAssignment4Family(
      withinComponentResolver, new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "ValidCompS53"
  })
  @Override
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModelsSubComponent")
  @MethodSource("invalidModelsWithVariability")
  @Override
  void shouldReportErrorSubComponents(@NotNull String model, @NotNull Error... errors) {
    this.shouldReportError(model, errors);
  }

  @ParameterizedTest
  @MethodSource("invalidModelsHead")
  @Override
  public void shouldReportErrorHead(@NotNull String model, @NotNull Error... errors) {
    this.shouldReportError(model, errors);
  }

  @ParameterizedTest
  @MethodSource("invalidModels4Refinement")
  @Override
  void shouldReportError4Refinement(@NotNull String model, @NotNull Error... errors) {
    this.shouldReportError(model, errors);
  }

  @Test
  void shouldWarnIfPresenceDependsOnUnknownValues() {
    // Given
    ASTMACompilationUnit ast = compile("""
      component UnknownComp(int n) {
        varif (java.lang.Math.abs(n) > 3) {
          a.b.B sub();
        }
      }""");
    assertThat(Log.getFindings()).as("findings of the compilation").isEmpty();

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).singleElement()
      .matches(Finding::isWarning)
      .extracting(Finding::getMsg).asString()
      .startsWith(TOO_FEW_ARGUMENTS.getErrorCode());
  }

  protected void shouldReportError(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  protected static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // missing argument only in a variant excluded by a constraint
      arg("""
        component ValidCompWithVariability1 {
          feature f;
          varif (f) {
            a.b.B sub();
          }
          constraint (!f);
        }"""
      ),
      // complete arguments in mutually exclusive variation points
      arg("""
        component ValidCompWithVariability2 {
          feature f;
          varif (f) {
            a.b.B sub(1);
          } else {
            a.b.D sub(1, 2);
          }
        }"""
      ),
      // missing argument of a super component of a component type without configurations
      arg("""
        component ValidCompWithVariability3 extends a.b.B {
          feature f;
          constraint (f && !f);
        }"""
      )
    );
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // missing argument of an optional subcomponent
      arg("""
        component InvalidCompWithVariability1 {
          feature f;
          varif (f) {
            a.b.B sub();
          }
        }""",
        TOO_FEW_ARGUMENTS
      ),
      // too many arguments of a subcomponent of a core feature
      arg("""
        component InvalidCompWithVariability2 {
          feature f;
          varif (f) {
            a.b.A sub(1);
          }
          constraint (f);
        }""",
        TOO_MANY_ARGUMENTS
      )
    );
  }
}
