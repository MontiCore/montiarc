/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._cocos.TransitionPreconditionsAreBoolean;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.SCError.PRECONDITION_NOT_BOOLEAN;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link TransitionPreconditionsAreBoolean}.
 */
class TransitionPreconditionsAreBooleanTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new TransitionPreconditionsAreBoolean());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new TransitionPreconditionsAreBoolean());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // component no behavior
      arg("""
        component Comp1 { }
        """
      ),
      // transition without precondition
      arg("""
        component Comp2 {
          automaton {
            initial state S;
            S -> S;
          }
        }
        """
      ),
      // transition with boolean precondition
      arg("""
        component Comp3 {
          automaton {
            initial state S;
            S -> S [true];
          }
        }
        """
      ),
      // multiple transitions with boolean preconditions
      arg("""
        component Comp4 {
          automaton {
            initial state S;
            S -> S [true];
            S -> S [false];
          }
        }
        """
      ),
      // transition precondition over boolean port
      arg("""
        component Comp5 {
          port in boolean i;
          automaton {
            initial state S;
            S -> S [i] i;
          }
        }
        """
      ),
      // transition precondition over boolean parameter
      arg("""
        component Comp6(boolean p) {
          automaton {
            initial state S;
            S -> S [p];
          }
        }
        """
      ),
      // transition precondition over boolean component field
      arg("""
        component Comp7 {
          boolean v = true;
          automaton {
            initial state S;
            S -> S [v];
          }
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // transition with non-boolean precondition
      arg("""
          component Comp1 {
            automaton {
              initial state S;
              S -> S [1];
            }
          }
          """,
        PRECONDITION_NOT_BOOLEAN
      ),
      // multiple transitions, one transition with non-boolean precondition
      arg("""
          component Comp2 {
            automaton {
              initial state S;
              S -> S [true];
              S -> S [1];
            }
          }
          """,
        PRECONDITION_NOT_BOOLEAN
      ),
      // two transitions with non-boolean preconditions
      arg("""
          component Comp3 {
            automaton {
              initial state S;
              S -> S [1];
              S -> S [1];
            }
          }""",
        PRECONDITION_NOT_BOOLEAN, PRECONDITION_NOT_BOOLEAN
      ),
      // transition with non-boolean precondition (port access)
      arg("""
          component Comp4 {
            port in int i;
            automaton {
              initial state S;
              S -> S [i];
            }
          }""",
        PRECONDITION_NOT_BOOLEAN
      ),
      // transition with non-boolean precondition (parameter access)
      arg("""
          component Comp5(int p) {
            automaton {
              initial state S;
              S -> S [p];
            }
          }""",
        PRECONDITION_NOT_BOOLEAN
      ),
      // transition with non-boolean precondition (variable access)
      arg("""
          component Comp6 {
            int v = 1;
            automaton {
              initial state S;
              S -> S [v];
            }
          }""",
        PRECONDITION_NOT_BOOLEAN
      )
    );
  }
}
