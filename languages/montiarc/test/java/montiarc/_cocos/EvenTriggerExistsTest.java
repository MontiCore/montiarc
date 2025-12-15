/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.EventTriggerExists;
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

import static montiarc.util.ArcAutomataError.CANT_FIND_MSG_EVENT_SYMBOL;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link EventTriggerExists}.
 */
class EvenTriggerExistsTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new EventTriggerExists());

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
    checker.addCoCo(new EventTriggerExists());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // component without automaton
      arg("component ValidComp1 { }"),
      // automaton without transitions
      arg("""
        component ValidComp2 {
          automaton {
            initial state S;
          }
        }"""
      ),
      // transition with epsilon transition (transition without msg-event)
      arg("""
        component ValidComp3 {
          port <<sync>> in int i;
          port <<sync>> out int o;
          automaton {
            initial state S;
            S -> S;
          }
        }"""
      ),
      // transition with msg-event (event port)
      arg("""
        component ValidComp4 {
          port in int i;
          port out int o;
          automaton {
            initial state S;
            S -> S i;
          }
        }"""
      ),
      // transition with msg-event (synchronous port)
      arg("""
        component ValidComp5 {
          port <<sync>> in int i;
          port <<sync>> out int o;
          automaton {
            initial state S;
            S -> S i;
          }
        }"""
      ),
      // two transitions with msg-event
      arg("""
        component ValidComp6 {
          port in int i1, i2;
          port out int o;
          automaton {
            initial state S;
            S -> S i1;
            S -> S i2;
          }
        }"""
      ),
      // nested transition with msg-event
      arg("""
        component ValidComp7 {
          port in int i;
          port out int o;
          automaton {
            initial state S {
              S -> S i;
            }
          }
        }"""
      ),
      // inner transition with msg-event
      arg("""
        component ValidComp8 {
          port in int i;
          port out int o;
          automaton {
            initial state S {
              -> i;
            }
          }
        }"""
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // transition with non-resolvable msg-event
      arg("""
          component InvalidComp1 {
            automaton {
              initial state S;
              S -> S i;
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL
      ),
      // two transition with non-resolvable msg-event
      arg("""
          component InvalidComp2 {
            automaton {
              initial state S;
              S -> S i;
              S -> S i;
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL,
        CANT_FIND_MSG_EVENT_SYMBOL
      ),
      // two transition with non-resolvable msg-events
      arg("""
          component InvalidComp3 {
            automaton {
              initial state S;
              S -> S i1;
              S -> S i2;
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL,
        CANT_FIND_MSG_EVENT_SYMBOL
      ),
      // nested transition with non-resolvable msg-event
      arg("""
          component InvalidComp4 {
            automaton {
              initial state S {
                S -> S i;
              }
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL
      ),
      // inner transition with non-resolvable msg-event
      arg("""
          component InvalidComp5 {
            automaton {
              initial state S {
                -> i;
              }
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL
      ),
      // outgoing ports are not resolvable as msg-events
      arg("""
          component InvalidComp6 {
            port in int i;
            port out int o;
            automaton {
              initial state S;
              S -> S o;
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL
      ),
      // component fields are not resolvable as msg-events
      arg("""
          component InvalidComp7 {
            int v = 0;
            automaton {
              initial state S;
              S -> S v;
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL
      ),
      // parameters are not resolvable as msg-events
      arg("""
          component InvalidComp8(int p) {
            automaton {
              initial state S;
              S -> S p;
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL
      ),
      // type-parameters are not resolvable as msg-events
      arg("""
          component InvalidComp9<T> {
            automaton {
              initial state S;
              S -> S T;
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL
      ),
      // states are not resolvable as msg-events
      arg("""
          component InvalidComp10 {
            automaton {
              initial state S;
              S -> S S;
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL
      )
    );
  }
}
