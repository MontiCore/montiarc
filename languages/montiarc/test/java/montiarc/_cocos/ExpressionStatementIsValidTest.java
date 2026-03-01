/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import de.monticore.statements.mccommonstatements.cocos.ExpressionStatementIsValid;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.MCError.EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ExpressionStatementIsValid}.
 */
class ExpressionStatementIsValidTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.addCoCo(new ExpressionStatementIsValid());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.addCoCo(new ExpressionStatementIsValid());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // no behavior
      arg("""
        component ValidComp1 { }
        """
      ),
      // single statement in transition
      arg("""
        component ValidComp2 {
          int v = 0;
          automaton {
            initial state S;
            S -> S / v = 1;
          }
        }
        """
      ),
      // block statement in transition
      arg("""
        component ValidComp3 {
          int v = 0;
          automaton {
            initial state S;
            S -> S / { v = 1; }
          }
        }
        """
      ),
      // single statement in nested transition
      arg("""
        component ValidComp4 {
          int v = 0;
          automaton {
            initial state S {
              S -> S / v = 1;
            }
          }
        }
        """
      ),
      // block statement in nested transition
      arg("""
        component ValidComp5 {
          int v = 0;
          automaton {
            initial state S {
              S -> S / { v = 1; }
            }
          }
        }
        """
      ),
      // single statement in inner transition
      arg("""
        component ValidComp6 {
          int v = 0;
          automaton {
            initial state S {
              -> / v = 1;
            }
          }
        }
        """
      ),
      // block statement in inner transition
      arg("""
        component ValidComp7 {
          int v = 0;
          automaton {
            initial state S {
              -> / { v = 1; }
            }
          }
        }
        """
      ),
      // single statement in entry action
      arg("""
        component ValidComp8 {
          int v = 0;
          automaton {
            initial state S {
              entry / v = 1;
            }
          }
        }
        """
      ),
      // block statement in entry action
      arg("""
        component ValidComp9 {
          int v = 0;
          automaton {
            initial state S {
              entry / { v = 1; }
            }
          }
        }
        """
      ),
      // single statement in exit action
      arg("""
        component ValidComp10 {
          int v = 0;
          automaton {
            initial state S {
              exit / v = 1;
            }
          }
        }
        """
      ),
      // block statement in exit action
      arg("""
        component ValidComp11 {
          int v = 0;
          automaton {
            initial state S {
              exit / { v = 1; }
            }
          }
        }
        """
      ),
      // single statement in do action
      arg("""
        component ValidComp12 {
          int v = 0;
          automaton {
            initial state S {
              do / v = 1;
            }
          }
        }
        """
      ),
      // block statement in do action
      arg("""
        component ValidComp13 {
          int v = 0;
          automaton {
            initial state S {
              do / { v = 1; }
            }
          }
        }
        """
      ),
      // statement in init block
      arg("""
        component ValidComp14 {
          int v = 0;
          init {
            v = 1;
          }
        }
        """
      ),
      // statement in compute block
      arg("""
        component ValidComp15 {
          int v = 0;
          compute {
            v = 1;
          }
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // type mismatch, single statement in transition
      arg("""
          component InvalidComp1 {
            int v = 0;
            automaton {
              initial state S;
              S -> S / v = true;
            }
          }
          """,
        EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES
      ),
      // type mismatch, block statement in transition
      arg("""
          component InvalidComp2 {
            int v = 0;
            automaton {
              initial state S;
              S -> S / { v = true; }
            }
          }
          """,
        EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES
      ),
      // type mismatch, single statement in nested transition
      arg("""
          component InvalidComp3 {
            int v = 0;
            automaton {
              initial state S {
                S -> S / v = true;
              }
            }
          }
          """,
        EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES
      ),
      // type mismatch, block statement in nested transition
      arg("""
          component InvalidComp4 {
            int v = 0;
            automaton {
              initial state S {
                S -> S / { v = true; }
              }
            }
          }
          """,
        EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES
      ),
      // type mismatch, single statement in inner transition
      arg("""
          component InvalidComp5 {
            int v = 0;
            automaton {
              initial state S {
                -> / v = true;
              }
            }
          }
          """,
        EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES
      ),
      // type mismatch, block statement in inner transition
      arg("""
          component InvalidComp6 {
            int v = 0;
            automaton {
              initial state S {
                -> / { v = true; }
              }
            }
          }
          """,
        EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES
      ),
      // type mismatch, single statement in entry action
      arg("""
          component InvalidComp7 {
            int v = 0;
            automaton {
              initial state S {
                entry / v = true;
              }
            }
          }
          """,
        EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES
      ),
      // type mismatch, block statement in entry action
      arg("""
          component InvalidComp8 {
            int v = 0;
            automaton {
              initial state S {
                entry / { v = true; }
              }
            }
          }
          """,
        EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES
      ),
      // type mismatch, single statement in exit action
      arg("""
          component InvalidComp9 {
            int v = 0;
            automaton {
              initial state S {
                exit / v = true;
              }
            }
          }
          """,
        EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES
      ),
      // type mismatch, block statement in exit action
      arg("""
          component InvalidComp10 {
            int v = 0;
            automaton {
              initial state S {
                exit / { v = true; }
              }
            }
          }
          """,
        EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES
      ),
      // type mismatch, single statement in do action
      arg("""
          component InvalidComp11 {
            int v = 0;
            automaton {
              initial state S {
                do / v = true;
              }
            }
          }
          """,
        EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES
      ),
      // type mismatch, block statement in do action
      arg("""
          component InvalidComp12 {
            int v = 0;
            automaton {
              initial state S {
                do / { v = true; }
              }
            }
          }
          """,
        EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES
      ),
      // type mismatch, statement in init block
      arg("""
          component InvalidComp13 {
            int v = 0;
            init {
              v = true;
            }
          }
          """,
        EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES
      ),
      // type mismatch, statement in compute block
      arg("""
          component InvalidComp14 {
            int v = 0;
            compute {
              v = true;
            }
          }
          """,
        EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES
      )
    );
  }
}
