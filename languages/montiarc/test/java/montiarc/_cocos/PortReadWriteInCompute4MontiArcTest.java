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
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static montiarc.util.ArcError.READ_FROM_OUTGOING_PORT;
import static montiarc.util.ArcError.WRITE_TO_INCOMING_PORT;
import static org.assertj.core.api.Assertions.assertThat;

class PortReadWriteInCompute4MontiArcTest extends MontiArcTestBase {

  final static String SYMBOLS_DIR = "symbols";

  @BeforeEach
  public void setUp() {
    MontiArcMill.globalScope().setSymbolPath(
      new MCPath(Paths.get(TEST_RESOURCE, SYMBOLS_DIR))
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // 1 - Write literal to output port in compute block
    "component Comp1 { " +
      "  port out int o; " +
      "  compute  { " +
      "    o = 0; " +
      "  } " +
      "}",
    // 2 - Write input to output port in compute block
    "component Comp2 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  compute { " +
      "    o = i; " +
      "  } " +
      "}",
    // 3 - Write value of variable to output port in compute block
    "component Comp3 { " +
      "  port out int o; " +
      "  int v = 0; " +
      "  compute { " +
      "    o = v; " +
      "  } " +
      "}",
    // 4 - Write value of input port to variable in compute block
    "component Comp4 { " +
      "  port in int i; " +
      "  int v = 0; " +
      "  compute { " +
      "    v = i; " +
      "  } " +
      "}",
    // 5 - Read value from and write to field in compute block
    "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp5 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO(); " +
      "  compute { " +
      "      o = v.i; o = v.o; " +
      "      v.i = 0; v.i +=1; v.i++; --v.i; " +
      "      v.o = 0; v.o +=1; v.o++; --v.o; " +
      "    " +
      "  } " +
      "}",
    // 6 - Read value from method call in compute block
    "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp6 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO(); " +
      "  compute { " +
      "      o = v.i(); o = v.o(); " +
      "  } " +
      "}",
    // 7 - Read value from and write to static field in compute block
    "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp7 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  compute { " +
      "      o = OOTypeWithStaticFieldIO.i; " +
      "      o = OOTypeWithStaticFieldIO.o; " +
      "    " +
      "  } " +
      "}",
    // 8 - Read value from static method call in compute block
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp8 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  compute { " +
      "    o = OOTypeWithStaticFunctionIO.i(); " +
      "    o = OOTypeWithStaticFunctionIO.o(); " +
      "  } " +
      "}",
    // 9 - Pass input as argument to static method call in compute block
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp9 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  compute { " +
      "    o = OOTypeWithStaticFunctionIO.i(i); " +
      "    o = OOTypeWithStaticFunctionIO.o(i); " +
      "  } " +
      "}",
    // 10 - Variable declaration shadows port in compute block
    "component Comp10 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  compute { " +
      "    int i = 0; " +
      "    o = i; " +
      "  } " +
      "}",
    // 11 - For control shadows port in compute block
    "component Comp11 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  compute { " +
      "    for (int i = 0; i < 10; i++) { " +
      "      o = i; " +
      "    } " +
      "  } " +
      "}",
    // 12 - Write input to output in switch statement block
    "component Comp12 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  compute { " +
      "    switch(i) { " +
      "      case 1: { " +
      "        o = i; " +
      "        break; " +
      "      }  " +
      "    }  " +
      "  } " +
      "}"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortReadWriteInCompute4MontiArc());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings())
      .as(() -> "Findings: " + Log.getFindings().toString())
      .isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model, @NotNull Error... expectedErrors) {
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

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // 1 - Do not write to incoming port in compute block
      arg("component Comp { " +
          "port in int i; " +
          "  compute { " +
          "    i = 0; " +
          "  }" +
          "}",
        WRITE_TO_INCOMING_PORT
      ),
      // 2 - Do not write to field of incoming port in compute block
      arg("component Comp2 { " +
          "port in montiarc.test.OOTypeWithField i; " +
          "  compute { " +
          "    i.v = 0; " +
          "  }" +
          "}",
        WRITE_TO_INCOMING_PORT),
      // 3 - Do not access field of outgoing port in compute block
      arg("component Comp3 { " +
          "port out montiarc.test.OOTypeWithField o; " +
          "  compute { " +
          "    o.v = 0; " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 4 - Do not access method of outgoing port in compute block
      arg("component Comp4 { " +
          "port out montiarc.test.OOTypeWithFunction o; " +
          "  compute { " +
          "    o.f(); " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 5 - Do not use port as argument of method call in compute block
      arg("component Comp5 { " +
          "port in montiarc.test.OOTypeWithFunction i; " +
          "port out int o; " +
          "  compute { " +
          "    i.f(o); " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 6 - Do not write to incoming port with inc suffix in compute block
      arg("component Comp6 { " +
          "port in int i; " +
          "  compute { " +
          "    i++; " +
          "  }" +
          "}",
        WRITE_TO_INCOMING_PORT),
      // 7 - Do not read from outgoing port with inc suffix in compute block
      arg("component Comp7 { " +
          "port out int o; " +
          "  compute { " +
          "    o++; " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 8 - Do not write to incoming port with dec suffix in compute block
      arg("component Comp8 { " +
          "port in int i; " +
          "  compute { " +
          "    i--; " +
          "  }" +
          "}",
        WRITE_TO_INCOMING_PORT),
      // 9 - Do not read from outgoing port with dec suffix in compute block
      arg("component Comp9 { " +
          "port out int o; " +
          "  compute { " +
          "    o--; " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 10 - Do not write to incoming port with inc prefix in compute block
      arg("component Comp10 { " +
          "port in int i; " +
          "  compute { " +
          "    ++i; " +
          "  }" +
          "}",
        WRITE_TO_INCOMING_PORT),
      // 11 - Do not read from outgoing port with inc prefix in compute block
      arg("component Comp11 { " +
          "port out int o; " +
          "  compute { " +
          "    ++o; " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 12 - Do not write to incoming port with dec prefix in compute block
      arg("component Comp12 { " +
          "port in int i; " +
          "  compute { " +
          "    --i; " +
          "  }" +
          "}",
        WRITE_TO_INCOMING_PORT),
      // 13 - Do not read from outgoing port with dec prefix in compute block
      arg("component Comp13 { " +
          "port out int o; " +
          "  compute { " +
          "    --o; " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 14 - Do not write to incoming port via assignment expression in compute block
      arg("component Comp14 { " +
          "port in int i1; " +
          "port in boolean i2; " +
          "  compute { " +
          "    i1 += 1; " +
          "    i1 -= 1; " +
          "    i1 *= 1; " +
          "    i1 /= 1; " +
          "    i1 %= 1; " +
          "    i1 >>= 1; " +
          "    i1 >>>= 1; " +
          "    i1 <<= 1; " +
          "    i2 &= true; " +
          "    i2 |= true; " +
          "    i2 ^= true; " +
          "  }" +
          "}",
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT,
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT,
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT,
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT
      ),
      // 15 - Do not read from outgoing port via assignment expression in compute block
      arg("component Comp15 { " +
          "port out int o1; " +
          "port out boolean o2; " +
          "int v1 = 0; " +
          "boolean v2 = true; " +
          "  compute { " +
          "    v1 += o1; " +
          "    v1 -= o1; " +
          "    v1 *= o1; " +
          "    v1 /= o1; " +
          "    v1 %= o1; " +
          "    v1 >>= o1; " +
          "    v1 >>>= o1; " +
          "    v1 <<= o1; " +
          "    v2 &= o2; " +
          "    v2 |= o2; " +
          "    v2 ^= o2; " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT
      ),
      // 16 - Do not read from outgoing port via infix expression in compute block
      arg("component Comp16 { " +
          "port out int o1; " +
          "port out boolean o2; " +
          "  compute { " +
          "    o1 + 1; " +
          "    o1 - 1; " +
          "    o1 * 1; " +
          "    o1 / 1; " +
          "    o1 % 1; " +
          "    o1 <= 1; " +
          "    o1 >= 1; " +
          "    o1 < 1; " +
          "    o1 > 1; " +
          "    o1 == 1; " +
          "    o1 != 1; " +
          "    o2 && true; " +
          "    o2 || true; " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT
      )
    );
  }
}
