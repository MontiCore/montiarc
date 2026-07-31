/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import de.monticore.statements.mccommonstatements.cocos.ForConditionHasBooleanType;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.MCError.FOR_CONDITION_NOT_BOOLEAN;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ForConditionHasBooleanType}.
 */
class ForConditionHasBooleanTypeTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ForConditionHasBooleanType());

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

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ForConditionHasBooleanType());

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
        component ValidComp1 { }
        """
      ),
      // for loop in transition
      arg("""
        component ValidComp2 {
          boolean v = false;
          automaton {
            initial state S;
            S -> S / {
              for (; v; ) { }
            }
          }
        }
        """
      ),
      // for loop in nested transition
      arg("""
        component ValidComp3 {
          boolean v = false;
          automaton {
            initial state S {
              S -> S / {
                for (; v; ) { }
              }
            }
          }
        }
        """
      ),
      // for loop in inner transition
      arg("""
        component ValidComp4 {
          boolean v = false;
          automaton {
            initial state S {
              -> / {
                for (; v; ) { }
              }
            }
          }
        }
        """
      ),
      // for loop in entry action
      arg("""
        component ValidComp5 {
          boolean v = false;
          automaton {
            initial state S {
              entry / {
                for (; v; ) { }
              }
            }
          }
        }
        """
      ),
      // for loop in exit action
      arg("""
        component ValidComp6 {
          boolean v = false;
          automaton {
            initial state S {
              exit / {
                for (; v; ) { }
              }
            }
          }
        }
        """
      ),
      // for loop in do action
      arg("""
        component ValidComp7 {
          boolean v = false;
          automaton {
            initial state S {
              do / {
                for (; v; ) { }
              }
            }
          }
        }
        """
      ),
      // for loop in init block
      arg("""
        component ValidComp8 {
          boolean v = false;
          init {
            for (; v; ) { }
          }
        }
        """
      ),
      // for loop in compute block
      arg("""
        component ValidComp9 {
          boolean v = false;
          compute {
            for (; v; ) { }
          }
        }
        """
      ),
      // for loop with empty condition
      arg("""
        component ValidComp10 {
          compute {
            for (; ; ) { }
          }
        }
        """
      ),
      // enhanced for loop does not define a condition to check
      arg("""
        component ValidComp11 {
          automaton {
            initial state S;
            S -> S / {
              for (int i : [1, 2]) { }
            }
          }
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // for condition not boolean in transition
      arg("""
          component InvalidComp1 {
            int v = 0;
            automaton {
              initial state S;
              S -> S / {
                for (; v; ) { }
              }
            }
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean nested transition
      arg("""
          component InvalidComp2 {
            int v = 0;
            automaton {
              initial state S {
                S -> S / {
                  for (; v; ) { }
                }
              }
            }
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean inner transition
      arg("""
          component InvalidComp3 {
            int v = 0;
            automaton {
              initial state S {
                -> / {
                  for (; v; ) { }
                }
              }
            }
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean entry action
      arg("""
          component InvalidComp4 {
            int v = 0;
            automaton {
              initial state S {
                entry / {
                  for (; v; ) { }
                }
              }
            }
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean exit action
      arg("""
          component InvalidComp5 {
            int v = 0;
            automaton {
              initial state S {
                exit / {
                  for (; v; ) { }
                }
              }
            }
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean do action
      arg("""
          component InvalidComp6 {
            int v = 0;
            automaton {
              initial state S {
                do / {
                  for (; v; ) { }
                }
              }
            }
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean init block
      arg("""
          component InvalidComp7 {
            int v = 0;
            init {
              for (; v; ) { }
            }
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean compute block
      arg("""
          component InvalidComp8 {
            int v = 0;
            compute {
              for (; v; ) { }
            }
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      )
    );
  }
}
