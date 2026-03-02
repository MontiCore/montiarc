/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;

import java.util.stream.Stream;

import static montiarc.util.SCError.PRECONDITION_NOT_BOOLEAN;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link TransitionPreconditionsAreBoolean4Family}.
 */
class TransitionPreconditionsAreBoolean4FamilyTest extends TransitionPreconditionsAreBooleanTest {

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "ValidCompWithVariability2 ",
    "ValidCompWithVariability3 "
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new TransitionPreconditionsAreBoolean4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new TransitionPreconditionsAreBoolean4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // transition with non-boolean precondition, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          int v = 0;
          varif (false) {
            automaton {
              initial state S;
              S -> S [v];
            }
          }
        }
        """
      ),
      // transition with non-boolean precondition, dead feature
      arg("""
        component ValidCompWithVariability2 {
          feature f;
          int v = 0;
          varif (f) {
            automaton {
              initial state S;
              S -> S [v];
            }
          }
          constraint (!f);
        }
        """
      ),
      // transition with conditionally typed precondition
      arg("""
        component ValidCompWithVariability3 {
          feature f1;
          feature f2;
          varif (f1) {
            boolean v = false;
          }
          else {
            int v = 0;
          }
          varif (f2) {
            automaton {
              initial state S;
              S -> S [v];
            }
          }
          constraint (!(f2 && !f1));
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // transition with non-boolean precondition, tautological variation point
      arg("""
          component InvalidCompWithVariability1 {
            int v = 0;
            varif (true) {
              automaton {
                initial state S;
                S -> S [v];
              }
            }
          }
          """,
        PRECONDITION_NOT_BOOLEAN
      ),
      // transition with non-boolean precondition, core feature
      arg("""
          component InvalidCompWithVariability2 {
            feature f;
            int v = 0;
            varif (f) {
              automaton {
                initial state S;
                S -> S [v];
              }
            }
            constraint (f);
          }
          """,
        PRECONDITION_NOT_BOOLEAN
      ),
      // transition with conditionally typed non-boolean precondition
      arg("""
          component InvalidCompWithVariability3 {
            feature f1;
            feature f2;
            varif (f1) {
              boolean v = false;
            }
            else {
              int v = 0;
            }
            varif (f2) {
              automaton {
                initial state S;
                S -> S [v];
              }
            }
          }
          """,
        PRECONDITION_NOT_BOOLEAN
      )
    );
  }
}
