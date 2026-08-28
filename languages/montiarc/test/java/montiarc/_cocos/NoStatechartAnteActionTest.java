/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.NoStatechartAnteAction;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.ArcAutomataError.STATECHART_ANTE_ACTION_NOT_SUPPORTED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoStatechartAnteAction}.
 */
class NoStatechartAnteActionTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoStatechartAnteAction());

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
    checker.addCoCo(new NoStatechartAnteAction());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      // automaton without ante action
      arg("""
        component ValidComp1 {
          automaton {
            initial state Init;
            state Ready;
            Init -> Ready;
          }
        }
        """
      ),
      // initial state with a proper entry action, not a bare ante action
      arg("""
        component ValidComp2 {
          automaton {
            initial state Init {
              entry / { x = 0; }
            }
            state S;
            Init -> S;
          }
        }
        """
      )
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // ante action with a statement, preceding the states
      arg("""
        component InvalidComp1 {
          port out int x;
          automaton {
            initial { x = 0; }
            state Init;
          }
        }
        """,
        STATECHART_ANTE_ACTION_NOT_SUPPORTED
      ),
      // empty ante action, preceding the states
      arg("""
        component InvalidComp2 {
          automaton {
            initial { }
            state A;
          }
        }
        """,
        STATECHART_ANTE_ACTION_NOT_SUPPORTED
      ),
      // ante action without the leading 'initial' keyword, in a nested component's automaton
      arg("""
        component InvalidComp3 {
          component Inner {
            automaton {
              { x = 1; }
              state Init;
            }
          }
        }
        """,
        STATECHART_ANTE_ACTION_NOT_SUPPORTED
      )
    );
  }
}
