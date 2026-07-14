/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.MontiArcError.BREAK_STATEMENT_TARGETS_NO_LOOP;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link BreakStatementTargetsLoop}.
 */
class BreakStatementTargetsLoopTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new BreakStatementTargetsLoop());
    checker.checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model) {
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new BreakStatementTargetsLoop());
    checker.checkAll(ast);

    assertThat(getLoggedErrorCodes()).containsExactly(BREAK_STATEMENT_TARGETS_NO_LOOP.getErrorCode());
  }

  static Stream<String> validModels() {
    return Stream.of(
      """
        component BreakInWhile {
          compute {
            while (true) {
              break;
            }
          }
        }
        """,
      """
        component BreakInIfInFor {
          compute {
            for (int i = 0; i < 10; i++) {
              if (i == 5) {
                break;
              }
            }
          }
        }
        """,
      """
        component BreakInDoWhile {
          compute {
            do {
              break;
            } while (true);
          }
        }
        """,
      """
        component BreakInLoopInsideSwitch {
          port sync in int i;
          compute {
            switch (i) {
              case 1: {
                while (true) {
                  break;
                }
              }
              default: { }
            }
          }
        }
        """,
      """
        component BreakInSwitchInsideLoop {
          port sync in int i;
          compute {
            while (true) {
              switch (i) {
                case 1: {
                  break;
                }
                default: { }
              }
            }
          }
        }
        """
    );
  }

  static Stream<String> invalidModels() {
    return Stream.of(
      """
        component BreakWithoutTarget {
          compute {
            break;
          }
        }
        """,
      """
        component BreakInSwitch {
          port sync in int i;
          compute {
            switch (i) {
              case 1: {
                break;
              }
              default: { }
            }
          }
        }
        """,
      """
        component BreakInIfInSwitch {
          port sync in int i;
          compute {
            switch (i) {
              case 1: {
                if (i == 1) {
                  break;
                }
              }
              default: { }
            }
          }
        }
        """
    );
  }
}
