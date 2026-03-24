/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

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

import static montiarc.util.ArcError.READ_FROM_OUTGOING_PORT;
import static montiarc.util.ArcError.WRITE_TO_INCOMING_PORT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link PortReadWriteInTransition4MontiArc}.
 */
class PortReadWriteInTransition4MontiArcTest extends MontiArcTestBase {

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
    checker.addCoCo(new PortReadWriteInTransition4MontiArc());

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
    checker.addCoCo(new PortReadWriteInTransition4MontiArc());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // write literal to output in transition action
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
      // write value of input to output in transition action
      arg("""
        component ValidComp2 {
          port in int i;
          port out int o;
          automaton {
            state S;
            S -> S i / { o = i; }
          }
        }
        """
      ),
      // write value of variable to output in transition action
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
      // write value of input to variable in transition action
      arg("""
        component ValidComp4 {
          port in int i;
          int v = 0;
          automaton {
            state S;
            S -> S i / { v = i; }
          }
        }
        """
      ),
      // read value from and write to field in transition action
      arg("""
        import montiarc.test.OOTypeWithFieldIO;
        component ValidComp5 {
          port in int i;
          port out int o;
          OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO();
          automaton {
            state S;
            S -> S i / {
              o = v.i; o = v.o;
              v.i = 0; v.i +=1; v.i++; --v.i;
              v.o = 0; v.o +=1; v.o++; --v.o;
            }
          }
        }
        """
      ),
      // read value from method call in transition action
      arg("""
        import montiarc.test.OOTypeWithFunctionIO;
        component ValidComp6 {
          port in int i;
          port out int o;
          OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO();
          automaton {
            state S;
            S -> S i / {
              o = v.i(); o = v.o();
            }
          }
        }
        """
      ),
      // read value from static field in transition action
      arg("""
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp7 {
          port in int i;
          port out int o;
          automaton {
            state S;
            S -> S i / {
              o = OOTypeWithStaticFieldIO.i;
              o = OOTypeWithStaticFieldIO.o;
            }
          }
        }
        """
      ),
      // read value from static method call in transition action
      arg("""
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp8 {
          port in int i;
          port out int o;
          automaton {
            state S;
            S -> S i / {
              o = OOTypeWithStaticFunctionIO.i();
              o = OOTypeWithStaticFunctionIO.o();
            }
          }
        }
        """
      ),
      // pass input as argument to static method call in transition action
      arg("""
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp9 {
          port in int i;
          port out int o;
          automaton {
            state S;
            S -> S i / {
              o = OOTypeWithStaticFunctionIO.i(i);
              o = OOTypeWithStaticFunctionIO.o(i);
            }
          }
        }
        """
      ),
      // variable declaration shadows port in transition action
      arg("""
        component ValidComp10 {
          port in int i;
          port out int o;
          automaton {
            state S;
            S -> S i / {
              int i = 0;
              o = i;
            }
          }
        }
        """
      ),
      // for control shadows port in transition action
      arg("""
        component ValidComp11 {
          port in int i;
          port out int o;
          automaton {
            state S;
            S -> S i / {
              for (int i = 0; i < 10; i++) {
                o = i;
              }
            }
          }
        }
        """
      ),
      // write value of input to output in transition action
      arg("""
        component ValidComp12 {
          port in int i;
          port out int o;
          automaton {
            state S;
            S -> S i / { o = i; }
          }
        }
        """
      ),
      // read value of input in transition guard
      arg("""
        component ValidComp13 {
          port in boolean i;
          automaton {
            state S;
            S -> S [i];
          }
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // write to input in transition action
      arg("""
          component InvalidComp1 {
            port in int i;
            automaton {
              state S;
              S -> S i / { i = 0; }
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // write to field of input in transition action
      arg("""
          component InvalidComp2 {
            port in montiarc.test.OOTypeWithField i;
            automaton {
              state S;
              S -> S i / { i.v = 0; }
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // access field of output in transition action
      arg("""
          component InvalidComp3 {
            port out montiarc.test.OOTypeWithField o;
            automaton {
              state S;
              S -> S / { o.v = 0; }
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // access method of output in transition action
      arg("""
          component InvalidComp4 {
            port out montiarc.test.OOTypeWithFunction o;
            automaton {
              state S;
              S -> S / { o.f(); }
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // pass output as argument of method call in transition action
      arg("""
          component InvalidComp5 {
            port in montiarc.test.OOTypeWithFunction i;
            port out int o;
            automaton {
              state S;
              S -> S i / { i.f(o); }
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to input with inc suffix in transition action
      arg("""
          component InvalidComp6 {
            port in int i;
            automaton {
              state S;
              S -> S i / { i++; }
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output with inc suffix in transition action
      arg("""
          component InvalidComp7 {
            port out int o;
            automaton {
              state S;
              S -> S / { o++; }
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to input with dec suffix in transition action
      arg("""
          component InvalidComp8 {
            port in int i;
            automaton {
              state S;
              S -> S i / { i--; }
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output with dec suffix in transition action
      arg("""
          component InvalidComp9 {
            port out int o;
            automaton {
              state S;
              S -> S / { o--; }
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to input with inc prefix in transition action
      arg("""
          component InvalidComp10 {
            port in int i;
            automaton {
              state S;
              S -> S i / { ++i; }
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output with inc prefix in transition action
      arg("""
          component InvalidComp11 {
            port out int o;
            automaton {
              state S;
              S -> S / { ++o; }
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to input with dec prefix in transition action
      arg("""
          component InvalidComp12 {
            port in int i;
            automaton {
              state S;
              S -> S i / { --i; }
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output with dec prefix in transition action
      arg("""
          component InvalidComp13 {
            port out int o;
            automaton {
              state S;
              S -> S / { --o; }
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to input via assignment expression in transition action
      arg("""
          component InvalidComp14 {
            port in int i1;
            port in boolean i2;
            automaton {
              state S;
              S -> S i / { i1 += 1; }
              S -> S i / { i1 -= 1; }
              S -> S i / { i1 *= 1; }
              S -> S i / { i1 /= 1; }
              S -> S i / { i1 %= 1; }
              S -> S i / { i1 >>= 1; }
              S -> S i / { i1 >>>= 1; }
              S -> S i / { i1 <<= 1; }
              S -> S i / { i2 &= true; }
              S -> S i / { i2 |= true; }
              S -> S i / { i2 ^= true; }
            }
          }
          """,
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT,
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT,
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT,
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT
      ),
      // read from output via assignment expression in transition action
      arg("""
          component InvalidComp15 {
            port out int o1;
            port out boolean o2;
            int v1 = 0;
            boolean v2 = true;
            automaton {
              state S;
              S -> S i / { v1 += o1; }
              S -> S i / { v1 -= o1; }
              S -> S i / { v1 *= o1; }
              S -> S i / { v1 /= o1; }
              S -> S i / { v1 %= o1; }
              S -> S i / { v1 >>= o1; }
              S -> S i / { v1 >>>= o1; }
              S -> S i / { v1 <<= o1; }
              S -> S i / { v2 &= o2; }
              S -> S i / { v2 |= o2; }
              S -> S i / { v2 ^= o2; }
            }
          }
          """,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT
      ),
      // read from output via infix expression in transition action
      arg("""
          component InvalidComp16 {
            port out int o1;
            port out boolean o2;
            automaton {
              state S;
              S -> S i / { o1 + 1; }
              S -> S i / { o1 - 1; }
              S -> S i / { o1 * 1; }
              S -> S i / { o1 / 1; }
              S -> S i / { o1 % 1; }
              S -> S i / { o1 <= 1; }
              S -> S i / { o1 >= 1; }
              S -> S i / { o1 < 1; }
              S -> S i / { o1 > 1; }
              S -> S i / { o1 == 1; }
              S -> S i / { o1 != 1; }
              S -> S i / { o2 && true; }
              S -> S i / { o2 || true; }
            }
          }
          """,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT
      ),
      // read from output in transition guard
      arg("""
          component InvalidComp17 {
            port out boolean o;
            automaton {
              state S;
              S -> S [o];
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      )
    );
  }
}
