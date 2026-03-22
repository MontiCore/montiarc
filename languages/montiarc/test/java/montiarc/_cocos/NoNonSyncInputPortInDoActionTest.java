/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.NoNonSyncInputPortInDoAction;
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
 * The class under test is {@link NoNonSyncInputPortInDoAction}.
 */
class NoNonSyncInputPortInDoActionTest extends MontiArcTestBase {

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
    checker.addCoCo(new NoNonSyncInputPortInDoAction());

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
    checker.addCoCo(new NoNonSyncInputPortInDoAction());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // write literal to output port in do action
      arg("""
        component ValidComp1 {
          port out int o;
          automaton {
            state S {
              do / { o = 0; }
            }
          }
        }
        """
      ),
      // write literal to synchronous output port in do action
      arg("""
        component ValidComp2 {
          port sync out int o;
          automaton {
            state S {
              do / { o = 0; }
            }
          }
        }
        """
      ),
      // write value of component variable to output port in do action
      arg("""
        component ValidComp3 {
          port out int o;
          int v = 0;
          automaton {
            state S {
              do / { o = v; }
            }
          }
        }
        """
      ),
      // write value of synchronous port to output port in do action
      arg("""
        component ValidComp4 {
          port sync in int i;
          port out int o;
          automaton {
            state S {
              do / { o = i; }
            }
          }
        }
        """
      ),
      // write value of synchronous ports to output port in do action
      arg("""
        component ValidComp5 {
          port sync in int i1, i2;
          port out int o;
          automaton {
            state S {
              do / { o = i1 + i2; }
            }
          }
        }
        """
      ),
      // read value from and write to field in do action
      arg("""
        import montiarc.test.OOTypeWithFieldIO;
        component ValidComp6 {
          port in int i;
          port out int o;
          OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO();
          automaton {
            state S {
              do / {
                o = v.i; o = v.o;
                v.i = 0; v.i +=1; v.i++; --v.i;
                v.o = 0; v.o +=1; v.o++; --v.o;
              }
            }
          }
        }
        """
      ),
      // read value from method call in do action
      arg("""
        import montiarc.test.OOTypeWithFunctionIO;
        component ValidComp7 {
          port in int i;
          port out int o;
          OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO();
          automaton {
            state S {
              do / {
                o = v.i(); o = v.o();
              }
            }
          }
        }
        """
      ),
      // read value from and write to static field in do action
      arg("""
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp8 {
          port in int i;
          port out int o;
          automaton {
            state S {
              do / {
                o = OOTypeWithStaticFieldIO.i;
                o = OOTypeWithStaticFieldIO.o;
              }
            }
          }
        }
        """
      ),
      // read value from static method call in do action
      arg("""
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp9 {
          port in int i;
          port out int o;
          automaton {
            state S {
              do / {
                o = OOTypeWithStaticFunctionIO.i();
                o = OOTypeWithStaticFunctionIO.o();
              }
            }
          }
        }
        """
      ),
      // variable declaration shadows port in do action
      arg("""
        component ValidComp10 {
          port in int i;
          port out int o;
          automaton {
            state S {
              do / {
                int i = 0;
                o = i;
              }
            }
          }
        }
        """
      ),
      // for control shadows port in do action
      arg("""
        component ValidComp11 {
          port in int i;
          port out int o;
          automaton {
            state S {
              do / {
                for (int i = 0; i < 10; i++) {
                  o = i;
                }
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
      // read from non-synchronous input port in var declaration statement in do action
      arg("""
          component InvalidComp1 {
            port in int i;
            automaton {
              state S {
                do / {
                  int x = i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in assignment expressions in do action
      arg("""
          component InvalidComp2 {
            port in int i;
            automaton {
              state S {
                do / {
                  int x = 0;
                  x = i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in inc prefix expressions in do action
      arg("""
          component InvalidComp3 {
            port in int i;
            automaton {
              state S {
                do / {
                  ++i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in dec prefix expressions in do action
      arg("""
          component InvalidComp4 {
            port in int i;
            automaton {
              state S {
                do / {
                  --i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in inc suffix expressions in do action
      arg("""
          component InvalidComp5 {
            port in int i;
            automaton {
              state S {
                do / {
                  i++;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in dec suffix expressions in do action
      arg("""
          component InvalidComp6 {
            port in int i;
            automaton {
              state S {
                do / {
                  i--;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in boolean not expressions in do action
      arg("""
          component InvalidComp7 {
            port in boolean i;
            automaton {
              state S {
                do / {
                  boolean x = true;
                  x = ~i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in logical not expressions in do action
      arg("""
          component InvalidComp8 {
            port in boolean i;
            automaton {
              state S {
                do / {
                  boolean x = true;
                  x = !i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in multiply expressions (right) in do action
      arg("""
          component InvalidComp9 {
            port in int i;
            automaton {
              state S {
                do / {
                  int x = 0;
                  x = 2 * i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in multiply expressions (left) in do action
      arg("""
          component InvalidComp10 {
            port in int i;
            automaton {
              state S {
                do / {
                  int x = 0;
                  x = i * 2;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in multiply expressions (both) in do action
      arg("""
          component InvalidComp11 {
            port in int i;
            automaton {
              state S {
                do / {
                  int x = 0;
                  x = i * i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in multiply expressions (both) in do action
      arg("""
          component InvalidComp12 {
            port in int i1;
            port sync in int i2;
            automaton {
              state S {
                do / {
                  int x = 0;
                  x = i1 * i2;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in infix expressions in do action
      arg("""
          component InvalidComp13 {
            port in int i1;
            port in boolean i2;
            automaton {
              state S {
                do / {
                  int x = 0;
                  boolean y = true;
                  x = 2 / i1;  x = i1 / 2;
                  x = 2 % i1;  x = i1 % 2;
                  x = 2 + i1;  x = i1 + 2;
                  x = 2 - i1;  x = i1 - 2;
                  y = i1 <= 2; y = 2 <= i1;
                  y = i1 >= 2; y = 2 >= i1;
                  y = i1 < 2;  y = 2 < i1;
                  y = i1 > 2;  y = 2 > i1;
                  y = i1 == 2; y = 2 == i1;
                  y = i1 != 2; y = 2 != i1;
                  y = i2 && 2; y = 2 && i2;
                  y = i2 || 2; y = 2 || i2;
                }
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
      // read from non-synchronous input port in conditional expressions (condition) in do action
      arg("""
          component InvalidComp14 {
            port in boolean i;
            automaton {
              state S {
                do / {
                  int x = 0;
                  x = i ? -2 : 2;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in conditional expressions (then) in do action
      arg("""
          component InvalidComp15 {
            port in int i;
            automaton {
              state S {
                do / {
                  int x = 0;
                  x = true ? i : 2;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in conditional expressions (else) in do action
      arg("""
          component InvalidComp16 {
            port in int i;
            automaton {
              state S {
                do / {
                  int x = 0;
                  x = true ? -2 : i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in bracket expressions in do action
      arg("""
          component InvalidComp17 {
            port in int i;
            automaton {
              state S {
                do / {
                  int x = (i);
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in shift expressions in do action
      arg("""
          component InvalidComp18 {
            port in int i;
            automaton {
              state S {
                do / {
                  int x = 0;
                  x = i << 1;  x = 1 << i;
                  x = i >> 1;  x = 1 >> i;
                  x = i >>> 1; x = 1 >>> i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in binary expressions in do action
      arg("""
          component InvalidComp19 {
            port in boolean i;
            automaton {
              state S {
                do / {
                  boolean x = true;
                  x = i & true; x = true & i;
                  x = i ^ true; x = true ^ i;
                  x = i | true; x = true | i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in if statement (condition) in do action
      arg("""
          component InvalidComp20 {
            port in boolean i;
            automaton {
              state S {
                do / {
                  if (i) { }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in if statement (then) in do action
      arg("""
          component InvalidComp21 {
            port in int i;
            automaton {
              state S {
                do / {
                  if (true) { int x = i; }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in if statement (else) in do action
      arg("""
          component InvalidComp22 {
            port in int i;
            automaton {
              state S {
                do / {
                  if (false) { } else { int x = i; }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in common for control (var dec) in do action
      arg("""
          component InvalidComp23 {
            port in int i;
            automaton {
              state S {
                do / {
                  for (int j = i; j > 10; j++) { }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in common for control (var assignment) in do action
      arg("""
          component InvalidComp24 {
            port in int i;
            automaton {
              state S {
                do / {
                  int j = 0;
                  for (j = i; j > 10; j++) { }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in common for control (condition) in do action
      arg("""
          component InvalidComp25 {
            port in int i;
            automaton {
              state S {
                do / {
                  for (int j = 1; i > 10; j++) { }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in common for control (expression) in do action
      arg("""
          component InvalidComp26 {
            port in int i;
            automaton {
              state S {
                do / {
                  for (int j = 0; j > 10; i++) { }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read from non-synchronous input port in common for control (expression) in do action
      arg("""
          component InvalidComp27 {
            port in int i;
            automaton {
              state S {
                do / {
                  for (int j = 0; j > 10; j++, i++) { }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      )
    );
  }
}
