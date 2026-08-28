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
 * The class under test is {@link PortReadWriteInCompute4MontiArc}.
 */
class PortReadWriteInCompute4MontiArcTest extends MontiArcTestBase {

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
    checker.addCoCo(new PortReadWriteInCompute4MontiArc());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... expectedErrors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    ASTMACompilationUnit ast = compile(model);
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortReadWriteInCompute4MontiArc());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // write literal to output in compute block
      arg("""
        component ValidComp1 {
          port sync out int o;
          compute  {
            o = 0;
          }
        }
        """
      ),
      // write input to output in compute block
      arg("""
        component ValidComp2 {
          port sync in int i;
          port sync out int o;
          compute {
            o = i;
          }
        }
        """
      ),
      // write value of variable to output in compute block
      arg("""
        component ValidComp3 {
          port sync out int o;
          int v = 0;
          compute {
            o = v;
          }
        }
        """
      ),
      // write value of input port to variable in compute block
      arg("""
        component ValidComp4 {
          port sync in int i;
          int v = 0;
          compute {
            v = i;
          }
        }
        """
      ),
      // read value from and write to field in compute block
      arg("""
        import montiarc.test.OOTypeWithFieldIO;
        component ValidComp5 {
          port sync in int i;
          port sync out int o;
          OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO();
          compute {
            o = v.i; o = v.o;
            v.i = 0; v.i +=1; v.i++; --v.i;
            v.o = 0; v.o +=1; v.o++; --v.o;
          }
        }
        """
      ),
      // read value from method call in compute block
      arg("""
        import montiarc.test.OOTypeWithFunctionIO;
        component ValidComp6 {
          port sync in int i;
          port sync out int o;
          OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO();
          compute {
            o = v.i(); o = v.o();
          }
        }
        """
      ),
      // read value from to static field in compute block
      arg("""
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp7 {
          port sync in int i;
          port sync out int o;
          compute {
            o = OOTypeWithStaticFieldIO.i;
            o = OOTypeWithStaticFieldIO.o;
          }
        }
        """
      ),
      // read value from static method call in compute block
      arg("""
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp8 {
          port sync in int i;
          port sync out int o;
          compute {
            o = OOTypeWithStaticFunctionIO.i();
            o = OOTypeWithStaticFunctionIO.o();
          }
        }
        """
      ),
      // pass input as argument to static method call in compute block
      arg("""
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp9 {
          port sync in int i;
          port sync out int o;
          compute {
            o = OOTypeWithStaticFunctionIO.i(i);
            o = OOTypeWithStaticFunctionIO.o(i);
          }
        }
        """
      ),
      // variable declaration shadows port in compute block
      arg("""
        component ValidComp10 {
          port sync out int o;
          port sync out int ov;
          compute {
            int ov = 0;
            o = ov;
          }
        }
        """
      ),
      // for control shadows port in compute block
      arg("""
        component ValidComp11 {
          port sync out int o;
          port sync out int ov;
          compute {
            for (int ov = 0; i < 10; i++) {
              o = ov;
            }
          }
        }
        """
      ),
      // write input to output in switch statement block
      arg("""
        component ValidComp12 {
          port sync in int i;
          port sync out int o;
          compute {
            switch(i) {
              case 1: {
                o = i;
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
      // write to input in compute block
      arg("""
          component InvalidComp1 {
            port in int i;
            compute {
              i = 0;
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // write to field of input in compute block
      arg("""
          component InvalidComp2 {
            port in montiarc.test.OOTypeWithField i;
            compute {
              i.v = 0;
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // access field of output in compute block
      arg("""
          component InvalidComp3 {
            port sync out montiarc.test.OOTypeWithField o;
            compute {
              o.v = 0;
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // access method of output in compute block
      arg("""
          component InvalidComp4 {
            port sync out montiarc.test.OOTypeWithFunction o;
            compute {
              o.f();
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // pass output as argument to method call in compute block
      arg("""
          component InvalidComp5 {
            port in montiarc.test.OOTypeWithFunction i;
            port sync out int o;
            compute {
              i.f(o);
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to input with inc suffix in compute block
      arg("""
          component InvalidComp6 {
            port in int i;
            compute {
              i++;
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output with inc suffix in compute block
      arg("""
          component InvalidComp7 {
            port sync out int o;
            compute {
              o++;
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to input with dec suffix in compute block
      arg("""
          component InvalidComp8 {
            port in int i;
            compute {
              i--;
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output with dec suffix in compute block
      arg("""
          component InvalidComp9 {
            port sync out int o;
            compute {
              o--;
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to input with inc prefix in compute block
      arg("""
          component InvalidComp10 {
            port in int i;
            compute {
              ++i;
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output with inc prefix in compute block
      arg("""
          component InvalidComp11 {
            port sync out int o;
            compute {
              ++o;
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to input with dec prefix in compute block
      arg("""
          component InvalidComp12 {
            port in int i;
            compute {
              --i;
            }
          }
          """,
        WRITE_TO_INCOMING_PORT
      ),
      // read from output with dec prefix in compute block
      arg("""
          component InvalidComp13 {
            port sync out int o;
            compute {
              --o;
            }
          }
          """,
        READ_FROM_OUTGOING_PORT
      ),
      // write to input with assignment expression in compute block
      arg("""
          component InvalidComp14 {
            port in int i1;
            port in boolean i2;
            compute {
              i1 += 1;
              i1 -= 1;
              i1 *= 1;
              i1 /= 1;
              i1 %= 1;
              i1 >>= 1;
              i1 >>>= 1;
              i1 <<= 1;
              i2 &= true;
              i2 |= true;
              i2 ^= true;
            }
          }
          """,
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT,
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT,
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT,
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT
      ),
      // read from output with assignment expression in compute block
      arg("""
          component InvalidComp15 {
            port sync out int o1;
            port sync out boolean o2;
            int v1 = 0;
            boolean v2 = true;
            compute {
              v1 += o1;
              v1 -= o1;
              v1 *= o1;
              v1 /= o1;
              v1 %= o1;
              v1 >>= o1;
              v1 >>>= o1;
              v1 <<= o1;
              v2 &= o2;
              v2 |= o2;
              v2 ^= o2;
            }
          }
          """,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT
      ),
      // read from output with infix expression in compute block
      arg("""
          component InvalidComp16 {
            port sync out int o1;
            port sync out boolean o2;
            compute {
              o1 + 1;
              o1 - 1;
              o1 * 1;
              o1 / 1;
              o1 % 1;
              o1 <= 1;
              o1 >= 1;
              o1 < 1;
              o1 > 1;
              o1 == 1;
              o1 != 1;
              o2 && true;
              o2 || true;
            }
          }
          """,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT
      )
    );
  }
}
