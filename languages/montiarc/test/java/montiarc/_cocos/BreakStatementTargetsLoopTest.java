/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.MontiArcError.BREAK_STATEMENT_TARGETS_NO_LOOP;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link BreakStatementTargetsLoop}.
 */
class BreakStatementTargetsLoopTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // break directly inside a while loop
    """
      component ValidComp1 {
        compute {
          while (true) {
            break;
          }
        }
      }
      """,
    // break inside an if nested in a for loop
    """
      component ValidComp2 {
        compute {
          for (int i = 0; i < 10; i++) {
            if (i == 5) {
              break;
            }
          }
        }
      }
      """,
    // break directly inside a do-while loop
    """
      component ValidComp3 {
        compute {
          do {
            break;
          } while (true);
        }
      }
      """,
    // break inside a while loop nested in a switch case
    """
      component ValidComp4 {
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
    // break inside a switch case nested in a while loop
    """
      component ValidComp5 {
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
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new BreakStatementTargetsLoop());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new BreakStatementTargetsLoop());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // break with no enclosing loop
      arg("""
        component InvalidComp1 {
          compute {
            break;
          }
        }
        """,
        BREAK_STATEMENT_TARGETS_NO_LOOP
      ),
      // break directly inside a switch case, no enclosing loop
      arg("""
        component InvalidComp2 {
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
        BREAK_STATEMENT_TARGETS_NO_LOOP
      ),
      // break inside an if nested in a switch case, no enclosing loop
      arg("""
        component InvalidComp3 {
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
        """,
        BREAK_STATEMENT_TARGETS_NO_LOOP
      ),
      // two breaks with no enclosing loop -> two errors
      arg("""
        component InvalidComp4 {
          compute {
            break;
            break;
          }
        }
        """,
        BREAK_STATEMENT_TARGETS_NO_LOOP,
        BREAK_STATEMENT_TARGETS_NO_LOOP
      )
    );
  }
}
