/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.NoInputPortInEntryAction;
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
 * The class under test is {@link NoInputPortInEntryAction}.
 */
class NoInputPortInEntryActionTest extends MontiArcTestBase {

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
    checker.addCoCo(new NoInputPortInEntryAction());

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
    checker.addCoCo(new NoInputPortInEntryAction());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // write literal to output port in entry action
      arg("""
        component ValidComp1 {
          port out int o;
          automaton {
            initial state S {
              entry / { o = 0; }
            }
          }
        }
        """
      ),
      // write literal to synchronous output port in entry action
      arg("""
        component ValidComp2 {
          port sync out int o;
          automaton {
            initial state S {
              entry / { o = 0; }
            }
          }
        }
        """
      ),
      // write value of component field to output port in entry action
      arg("""
        component ValidComp3 {
          port out int o;
          int v = 0;
          automaton {
            initial state S {
              entry / { o = v; }
            }
          }
        }
        """
      ),
      // read value from and write to field in entry action
      arg("""
        import montiarc.test.OOTypeWithFieldIO;
        component ValidComp4 {
          port in int i;
          port out int o;
          OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO();
          automaton {
            state S {
              entry / {
                o = v.i;
                o = v.o;
                v.i = 0; v.i++; v.i--; ++v.i; --v.i;
                v.o = 0; v.o++; v.o--; ++v.o; --v.o;
              }
            }
          }
        }
        """
      ),
      // read value from method call in entry action
      arg("""
        import montiarc.test.OOTypeWithFunctionIO;
        component ValidComp5 {
          port in int i;
          port out int o;
          OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO();
          automaton {
            state S {
              entry / {
                o = v.i();
                o = v.o();
              }
            }
          }
        }
        """
      ),
      // read value from and write to static field in entry action
      arg("""
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp6 {
          port in int i;
          port out int o;
          automaton {
            state S {
              entry / {
                o = OOTypeWithStaticFieldIO.i;
                o = OOTypeWithStaticFieldIO.o;
              }
            }
          }
        }
        """
      ),
      // read value from static method call in entry action
      arg("""
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp7 {
          port in int i;
          port out int o;
          automaton {
            state S {
              entry / {
                o = OOTypeWithStaticFunctionIO.i();
                o = OOTypeWithStaticFunctionIO.o();
              }
            }
          }
        }
        """
      ),
      // variable declaration shadows port in entry action
      arg("""
        component ValidComp8 {
          port in int i;
          port out int o;
          automaton {
            state S {
              entry / {
                int i = 0;
                o = i;
              }
            }
          }
        }
        """
      ),
      // for control shadows port in entry action
      arg("""
        component ValidComp9 {
          port in int i;
          port out int o;
          automaton {
            state S {
              entry / {
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

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // read value from input port in variable declaration in entry action
      arg("""
          component InvalidComp1 {
            port in int i;
            automaton {
              state S {
                entry / {
                  int x = i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from synchronous input port in variable declaration in entry action
      arg("""
          component InvalidComp2 {
            port sync in int i;
            automaton {
              state S {
                entry / {
                  int x = i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from input port in assignment expression in entry action
      arg("""
          component InvalidComp3 {
            port in int i;
            automaton {
              state S {
                entry / {
                  int x = 0;
                  x = i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in inc prefix expressions in entry action
      arg("""
          component InvalidComp4 {
            port in int i;
            automaton {
              state S {
                entry / {
                  ++i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in dec prefix expressions in entry action
      arg("""
          component InvalidComp5 {
            port in int i;
            automaton {
              state S {
                entry / {
                  --i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in inc suffix expressions in entry action
      arg("""
          component InvalidComp6 {
            port in int i;
            automaton {
              state S {
                entry / {
                  i++;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in dec suffix expressions in entry action
      arg("""
          component InvalidComp7 {
            port in int i;
            automaton {
              state S {
                entry / {
                  i--;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in boolean not expressions in entry action
      arg("""
          component InvalidComp8 {
            port in boolean i;
            automaton {
              state S {
                entry / {
                  boolean x = true;
                  x = ~i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in logical not expressions in entry action
      arg("""
          component InvalidComp9 {
            port in boolean i;
            automaton {
              state S {
                entry / {
                  boolean x = true;
                  x = !i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in multiply expressions (right) in entry action
      arg("""
          component InvalidComp10 {
            port in int i;
            automaton {
              state S {
                entry / {
                  int x = 0;
                  x = 2 * i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in multiply expressions (left) in entry action
      arg("""
          component InvalidComp11 {
            port in int i;
            automaton {
              state S {
                entry / {
                  int x = 0;
                  x = i * 2;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in multiply expressions (both) in entry action
      arg("""
          component InvalidComp12 {
            port in int i;
            automaton {
              state S {
                entry / {
                  int x = 0;
                  x = i * i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in infix expressions in entry action
      arg("""
          component InvalidComp13 {
            port in int i;
            automaton {
              state S {
                entry / {
                  int x = 0;
                  boolean y = true;
                  x = 2 / i;  x = i / 2;
                  x = 2 % i;  x = i % 2;
                  x = 2 + i;  x = i + 2;
                  x = 2 - i;  x = i - 2;
                  y = i <= 2; y = 2 <= i;
                  y = i >= 2; y = 2 >= i;
                  y = i < 2;  y = 2 < i;
                  y = i > 2;  y = 2 > i;
                  y = i == 2; y = 2 == i;
                  y = i != 2; y = 2 != i;
                  y = i && 2; y = 2 && i;
                  y = i || 2; y = 2 || i;
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
      // read value from port in conditional expressions (condition) in entry action
      arg("""
          component InvalidComp14 {
            port in int i;
            automaton {
              state S {
                entry / {
                  int x = 0;
                  x = i ? -2 : 2;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in conditional expressions (then) in entry action
      arg("""
          component InvalidComp15 {
            port in int i;
            automaton {
              state S {
                entry / {
                  int x = 0;
                  x = true ? i : 2;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in conditional expressions (else) in entry action
      arg("""
          component InvalidComp16 {
            port in int i;
            automaton {
              state S {
                entry / {
                  int x = 0;
                  x = true ? -2 : i;
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in bracket expressions in entry action
      arg("""
          component InvalidComp17 {
            port in int i;
            automaton {
              state S {
                entry / {
                  int x = (i);
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in shift expressions in entry action
      arg("""
          component InvalidComp18 {
            port in int i;
            automaton {
              state S {
                entry / {
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
      // read value from port in binary expressions in entry action
      arg("""
          component InvalidComp19 {
            port in boolean i;
            automaton {
              state S {
                entry / {
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
      // read value from port in if statement (condition) in entry action
      arg("""
          component InvalidComp20 {
            port in boolean i;
            automaton {
              state S {
                entry / {
                  if (i) { }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in if statement (then) in entry action
      arg("""
          component InvalidComp21 {
            port in boolean i;
            automaton {
              state S {
                entry / {
                  if (true) { int x = i; }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in if statement (else) in entry action
      arg("""
          component InvalidComp22 {
            port in boolean i;
            automaton {
              state S {
                entry / {
                  if (false) { } else { int x = i; }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in common for control (var dec) in entry action
      arg("""
          component InvalidComp23 {
            port in boolean i;
            automaton {
              state S {
                entry / {
                  for (int j = i; j > 10; j++) { }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in common for control (var assignment) in entry action
      arg("""
          component InvalidComp24 {
            port in boolean i;
            automaton {
              state S {
                entry / {
                  int j = 0;
                  for (j = i; j > 10; j++) { }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in common for control (condition) in entry action
      arg("""
          component InvalidComp25 {
            port in boolean i;
            automaton {
              state S {
                entry / {
                  for (int j = 0; i > 10; j++) { }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in common for control (expression) in entry action
      arg("""
          component InvalidComp26 {
            port in boolean i;
            automaton {
              state S {
                entry / {
                  for (int j = 0; j > 10; i++) { }
                }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from port in common for control (second expression) in entry action
      arg("""
          component InvalidComp27 {
            port in boolean i;
            automaton {
              state S {
                entry / {
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
