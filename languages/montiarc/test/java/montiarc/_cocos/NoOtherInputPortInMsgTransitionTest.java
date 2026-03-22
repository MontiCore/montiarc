/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.NoOtherInputPortInMsgTransition;
import com.google.common.base.Preconditions;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoOtherInputPortInMsgTransition}.
 */
class NoOtherInputPortInMsgTransitionTest extends MontiArcTestBase {

  private final static String SYMBOLS_DIR = "symbols";

  @BeforeEach
  @Override
  protected void init() {
    super.init();
    MontiArcMill.globalScope().setSymbolPath(new MCPath(Paths.get(TEST_RESOURCE, SYMBOLS_DIR)));
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoOtherInputPortInMsgTransition());

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
    checker.addCoCo(new NoOtherInputPortInMsgTransition());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // write literal to output port in epsilon transition
      arg("""
        component ValidComp1 {
          port out int o;
          automaton {
            state S;
            S -> S / { o = 0; }
          }
        }
        """
      ),
      // write literal to output port in message-event triggered transition
      arg("""
        component ValidComp2 {
          port out int i;
          port out int o;
          automaton {
            state S;
            S -> S i / { o = 0; }
          }
        }
        """
      ),
      // write value of component variable to output port in epsilon transition
      arg("""
        component ValidComp3 {
          port out int o;
          int v = 0;
          automaton {
            state S;
            S -> S / { o = v; }
          }
        }
        """
      ),
      // write value of component variable to output port in message-event triggered transition
      arg("""
        component ValidComp4 {
          port out int i;
          port out int o;
          int v = 0;
          automaton {
            state S;
            S -> S i / { o = v; }
          }
        }
        """
      ),
      // write value of message to output port in epsilon transition
      arg("""
        component ValidComp5 {
          port sync in int i;
          port out int o;
          automaton {
            state S;
            S -> S / { o = i; }
          }
        }
        """
      ),
      // write value of message to output port in message-event triggered transition
      arg("""
        component ValidComp6 {
          port in int i;
          port out int o;
          automaton {
            state S;
            S -> S i / { o = i; }
          }
        }
        """
      ),
      // write value of message to output port in message-event triggered transition
      arg("""
        component ValidComp7 {
          port in int i1, i2;
          port out int o;
          automaton {
            state S;
            S -> S i1 / { o = i1; }
            S -> S i2 / { o = i2; }
          }
        }
        """
      ),
      // read value from and write to field in epsilon transition
      arg("""
        import montiarc.test.OOTypeWithFieldIO;
        component ValidComp8 {
          port in int i;
          port out int o;
          OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO();
          automaton {
            state S;
            S -> S / {
              o = v.i; o = v.o;
              v.i = 0; v.i +=1; v.i++; --v.i;
              v.o = 0; v.o +=1; v.o++; --v.o;
            }
          }
        }
        """
      ),
      // read value from and write to field in message-event triggered transition
      arg("""
        import montiarc.test.OOTypeWithFieldIO;
        component ValidComp9 {
          port in int i, j;
          port out int o;
          OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO();
          automaton {
            state S;
            S -> S j / {
              o = v.i; o = v.o;
              v.i = 0; v.i +=1; v.i++; --v.i;
              v.o = 0; v.o +=1; v.o++; --v.o;
            }
          }
        }
        """
      ),
      // read value from method call in epsilon transition
      arg("""
        import montiarc.test.OOTypeWithFunctionIO;
        component ValidComp10 {
          port in int i;
          port out int o;
          OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO();
          automaton {
            state S;
            S -> S / {
              o = v.i(); o = v.o();
            }
          }
        }
        """
      ),
      // read value from method call in message-event triggered transition
      arg("""
        import montiarc.test.OOTypeWithFunctionIO;
        component ValidComp10 {
          port in int i, j;
          port out int o;
          OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO();
          automaton {
            state S;
            S -> S j / {
              o = v.i(); o = v.o();
            }
          }
        }
        """
      ),
      // read value from and write to static field in epsilon transition
      arg("""
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp12 {
          port in int i;
          port out int o;
          automaton {
            state S;
            S -> S / {
              o = OOTypeWithStaticFieldIO.i;
              o = OOTypeWithStaticFieldIO.o;
            }
          }
        }
        """
      ),
      // read value from and write to static field in message-event triggered transition
      arg("""
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp13 {
          port in int i, j;
          port out int o;
          automaton {
            state S;
            S -> S j / {
              o = OOTypeWithStaticFieldIO.i;
              o = OOTypeWithStaticFieldIO.o;
            }
          }
        }
        """
      ),
      // read value from static method call in epsilon transition
      arg("""
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp14 {
          port in int i;
          port out int o;
          automaton {
            state S;
            S -> S / {
              o = OOTypeWithStaticFunctionIO.i();
              o = OOTypeWithStaticFunctionIO.o();
            }
          }
        }
        """
      ),
      // read value from static method call in message-event triggered transition
      arg("""
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp15 {
          port in int i, j;
          port out int o;
          automaton {
            state S;
            S -> S j / {
              o = OOTypeWithStaticFunctionIO.i();
              o = OOTypeWithStaticFunctionIO.o();
            }
          }
        }
        """
      ),
      // variable declaration shadows port in epsilon transition
      arg("""
        component ValidComp16 {
          port in int i;
          port out int o;
          automaton {
            state S;
            S -> S / {
              int i = 0;
              o = i;
            }
          }
        }
        """
      ),
      // variable declaration shadows port in message-event triggered transition
      arg("""
        component ValidComp17 {
          port in int i, j;
          port out int o;
          automaton {
            state S;
            S -> S j / {
              int i = 0;
              o = i;
            }
          }
        }
        """
      ),
      // for control shadows port in epsilon transition
      arg("""
        component ValidComp18 {
          port in int i;
          port out int o;
          automaton {
            state S;
            S -> S / {
              for (int i = 0; i < 10; i++) {
                o = i;
              }
            }
          }
        }
        """
      ),
      // for control shadows port in message-event triggered transition
      arg("""
        component ValidComp19 {
          port in int i, j;
          port out int o;
          automaton {
            state S;
            S -> S j / {
              for (int i = 0; i < 10; i++) {
                o = i;
              }
            }
          }
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // read from other input port in var declaration statement in message-event triggered transition
      arg("""
          component Comp1 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                int x = i2;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in assignment expressions in message-event triggered transition
      arg("""
          component Comp2 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                int x = 0;
                x = i2;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in inc prefix expressions in message-event triggered transition
      arg("""
          component Comp3 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                ++i2;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in dec prefix expressions in message-event triggered transition
      arg("""
          component Comp4 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                --i2;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in inc suffix expressions in message-event triggered transition
      arg("""
          component Comp5 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                i2++;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in dec suffix expressions in message-event triggered transition
      arg("""
          component Comp6 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                i2--;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in boolean not expressions in message-event triggered transition
      arg("""
          component Comp7 {
            port in boolean i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                boolean x = true;
                x = ~i2;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in logical not expressions in message-event triggered transition
      arg("""
          component Comp8 {
            port in boolean i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                boolean x = true;
                x = !i2;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in multiply expressions (right) in message-event triggered transition
      arg("""
          component Comp9 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                int x = 0;
                x = 2 * i2;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in multiply expressions (left) in message-event triggered transition
      arg("""
          component Comp10 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                int x = 0;
                x = i2 * 2;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in multiply expressions (both) in message-event triggered transition
      arg("""
          component Comp11 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                int x = 0;
                x = i2 * i2;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in multiply expressions (both) in message-event triggered transition
      arg("""
          component Comp12 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i2 / {
                int x = 0;
                x = i1 * i1;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in infix expressions in message-event triggered transition
      arg("""
          component Comp13 {
            port in int i1, i2;
            port in boolean i3, i4;
            automaton {
              state S;
              S -> S i1 / {
                int x = 0;
                boolean y = true;
                x = 2 / i2;  x = i2 / 2;
                x = 2 % i2;  x = i2 % 2;
                x = 2 + i2;  x = i2 + 2;
                x = 2 - i2;  x = i2 - 2;
                y = i2 <= 2; y = 2 <= i2;
                y = i2 >= 2; y = 2 >= i2;
                y = i2 < 2;  y = 2 < i2;
                y = i2 > 2;  y = 2 > i2;
                y = i2 == 2; y = 2 == i2;
                y = i2 != 2; y = 2 != i2;
              }
              S -> S i3 / {
                y = i4 && 2; y = 2 && i4;
                y = i4 || 2; y = 2 || i4;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in conditional expressions (condition) in message-event triggered transition
      arg("""
          component Comp14 {
            port in boolean i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                int x = 0;
                x = i2 ? -2 : 2;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in conditional expressions (then) in message-event triggered transition
      arg("""
          component Comp15 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                int x = 0;
                x = true ? i2 : 2;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in conditional expressions (else) in message-event triggered transition
      arg("""
          component Comp16 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                int x = 0;
                x = true ? -2 : i2;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in bracket expressions in message-event triggered transition
      arg("""
          component Comp17 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                int x = (i2);
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in shift expressions in message-event triggered transition
      arg("""
          component Comp18 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                int x = 0;
                x = i2 << 1;  x = 1 << i2;
                x = i2 >> 1;  x = 1 >> i2;
                x = i2 >>> 1; x = 1 >>> i2;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in binary expressions in message-event triggered transition
      arg("""
          component Comp19 {
            port in boolean i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                boolean x = true;
                x = i2 & true; x = true & i2;
                x = i2 ^ true; x = true ^ i2;
                x = i2 | true; x = true | i2;
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in if statement (condition) in message-event triggered transition
      arg("""
          component Comp20 {
            port in boolean i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                if (i2) { }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in if statement (then) in message-event triggered transition
      arg("""
          component Comp21 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                if (true) { int x = i2; }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in if statement (else) in message-event triggered transition
      arg("""
          component Comp22 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                if (true) { } else { int x = i2; }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in common for control (var dec) in message-event triggered transition
      arg("""
          component Comp23 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                for (int j = i2; j > 10; j++) { }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in common for control (var assignment) in message-event triggered transition
      arg("""
          component Comp24 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                int j = 0;
                for (j = i2; j > 10; j++) { }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in common for control (condition) in message-event triggered transition
      arg("""
          component Comp25 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                for (int j = 1; i2 > 10; j++) { }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in common for control (expression) in message-event triggered transition
      arg("""
          component Comp26 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                for (int j = 0; j > 10; i2++) { }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from other input port in common for control (expression) in message-event triggered transition
      arg("""
          component Comp27 {
            port in int i1, i2;
            automaton {
              state S;
              S -> S i1 / {
                for (int j = 0; j > 10; j++, i2++) { }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      )

    );
  }
}
