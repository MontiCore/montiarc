/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.expressions.assignmentexpressions.cocos.AssignmentExpressionsOnlyAssignToLValuesCoCo;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.MCError.EXPRESSION_LVALUE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link AssignmentExpressionsOnlyAssignToLValuesCoCo}.
 */
class AssignmentExpressionsOnlyAssignToLValuesCoCoTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((AssignmentExpressionsASTAssignmentExpressionCoCo) new AssignmentExpressionsOnlyAssignToLValuesCoCo());

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
    checker.addCoCo((AssignmentExpressionsASTAssignmentExpressionCoCo) new AssignmentExpressionsOnlyAssignToLValuesCoCo());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      // no assignment expression
      arg("component ValidComp1 { }"),
      // assignment expression with parameter
      arg(
        """
          component ValidComp2(int p) {
            automaton {
              initial state s;
              s -> s / {
                p = 1;
              }
            }
          }
          """
      ),
      // assignment expression with field
      arg(
        """
          component ValidComp3 {
            int f = 1;
            automaton {
              initial state s;
              s -> s / {
                f = 1;
              }
            }
          }
          """
      ),
      // assignment expression with local variable
      arg(
        """
          component ValidComp4 {
            automaton {
              initial state s;
              s -> s / {
                int v = 1;
                v = 2;
              }
            }
          }
          """
      ),
      // assignment expression with port
      arg(
        """
          component ValidComp5 {
            port out int o;
            automaton {
              initial state s;
              s -> s / {
                o = 2;
              }
            }
          }
          """
      ),
      // increment expression with parameter
      arg(
        """
          component ValidComp6(int p) {
            automaton {
              initial state s;
              s -> s / {
                p++;
              }
            }
          }
          """
      ),
      // increment expression with field
      arg(
        """
          component ValidComp7 {
            int f = 1;
            automaton {
              initial state s;
              s -> s / {
                f++;
              }
            }
          }
          """
      ),
      // increment expression with local variable
      arg(
        """
          component ValidComp8 {
            automaton {
              initial state s;
              s -> s / {
                int v = 1;
                v++;
              }
            }
          }
          """
      ),
      // decrement expression with parameter
      arg(
        """
          component ValidComp9(int p) {
            automaton {
              initial state s;
              s -> s / {
                p--;
              }
            }
          }
          """
      ),
      // decrement expression with field
      arg(
        """
          component ValidComp10 {
            int f = 1;
            automaton {
              initial state s;
              s -> s / {
                f--;
              }
            }
          }
          """
      ),
      // decrement expression with local variable
      arg(
        """
          component ValidComp11 {
            automaton {
              initial state s;
              s -> s / {
                int v = 1;
                v--;
              }
            }
          }
          """
      ),
      // prefix increment expression with parameter
      arg(
        """
          component ValidComp12(int p) {
            automaton {
              initial state s;
              s -> s / {
                ++p;
              }
            }
          }
          """
      ),
      // prefix increment expression with field
      arg(
        """
          component ValidComp13 {
            int f = 1;
            automaton {
              initial state s;
              s -> s / {
                ++f;
              }
            }
          }
          """
      ),
      // prefix increment expression with local variable
      arg(
        """
          component ValidComp14 {
            automaton {
              initial state s;
              s -> s / {
                int v = 1;
                ++v;
              }
            }
          }
          """
      ),
      // prefix decrement expression with parameter
      arg(
        """
          component ValidComp15(int p) {
            automaton {
              initial state s;
              s -> s / {
                --p;
              }
            }
          }
          """
      ),
      // prefix decrement expression with field
      arg(
        """
          component ValidComp16 {
            int f = 1;
            automaton {
              initial state s;
              s -> s / {
                --f;
              }
            }
          }
          """
      ),
      // prefix decrement expression with local variable
      arg(
        """
          component ValidComp17 {
            automaton {
              initial state s;
              s -> s / {
                int v = 1;
                --v;
              }
            }
          }
          """
      ),
      // assignment, increment, and decrement expressions in a compute block
      arg(
        """
          component ValidComp18(int p) {
            port out int o;
            int f = 1;
            compute {
              int v = 0;
              p = 1; f = 1; v = 1; o = 1;
              p++; f++; v++; o++;
              p--; f--; v--; o--;
              ++p; ++f; ++v; ++o;
              --p; --f; --v; --o;
            }
          }
          """
      ),
      // assignment, increment, and decrement expressions nested within another assignment expression
      arg(
        """
          component ValidComp19 {
            port out int o;
            automaton {
              initial state s;
              s -> s / {
                int v = 1;
                o = v = 1;
                o = v++;
                o = v--;
                o = ++v;
                o = --v;
                o = o = v = v;
              }
            }
          }
          """
      )
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // assignment expression with literal value
      arg(
        """
          component InvalidComp1 {
            automaton {
              initial state s;
              s -> s / {
                0 = 1;
              }
            }
          }
          """,
        EXPRESSION_LVALUE
      ),
      // increment expression with literal value
      arg(
        """
          component InvalidComp2 {
            automaton {
              initial state s;
              s -> s / {
                0++;
              }
            }
          }
          """,
        EXPRESSION_LVALUE
      ),
      // decrement expression with literal value
      arg(
        """
          component InvalidComp3 {
            automaton {
              initial state s;
              s -> s / {
                0--;
              }
            }
          }
          """,
        EXPRESSION_LVALUE
      ),
      // prefix increment expression with literal value
      arg(
        """
          component InvalidComp4 {
            automaton {
              initial state s;
              s -> s / {
                ++0;
              }
            }
          }
          """,
        EXPRESSION_LVALUE
      ),
      // prefix decrement expression with literal value
      arg(
        """
          component InvalidComp5 {
            automaton {
              initial state s;
              s -> s / {
                --0;
              }
            }
          }
          """,
        EXPRESSION_LVALUE
      ),
      // nested assignment expression with literal value
      arg(
        """
          component InvalidComp6 {
            automaton {
              initial state s;
              s -> s / {
                int i = 1++;
              }
            }
          }
          """,
        EXPRESSION_LVALUE
      ),
      // multiple nested assignment expression with literal value
      arg(
        """
          component InvalidComp7 {
            automaton {
              initial state s;
              s -> s / {
                1++ = 1 = 1;
              }
            }
          }
          """,
        EXPRESSION_LVALUE,
        EXPRESSION_LVALUE,
        EXPRESSION_LVALUE
      ),
      // assignment expression with function call
      arg(
        """
          component InvalidComp8 {
            automaton {
              initial state s;
              s -> s / {
                func() = 1;
              }
            }
          }
          """,
        EXPRESSION_LVALUE
      )
    );
  }
}
