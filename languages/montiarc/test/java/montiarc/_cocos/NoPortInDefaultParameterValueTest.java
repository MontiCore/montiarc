/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.NoPortInDefaultParameterValue;
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

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

class NoPortInDefaultParameterValueTest extends MontiArcTestBase {

  final static String SYMBOLS_DIR = "symbols";

  @BeforeEach
  public void setUp() {
    MontiArcMill.globalScope().setSymbolPath(
      new MCPath(Paths.get(TEST_RESOURCE, SYMBOLS_DIR))
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // 1 - No ports, no expressions
    "component Comp1 { }",
    // 2 - No expressions
    "component Comp2 { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 3 - Component with default parameter value, read from literal
    "component Comp3(int p = 1) { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 4 - Component with default parameter value (two parameters), read from literal
    "component Comp4(int p1 = 1, int p2 = 2) { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 5 - Component with default parameter value, read from static field
    "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp5(int p = OOTypeWithStaticFieldIO.i) { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 6 - Component with default parameter value, read from static field
    "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp6(int p = OOTypeWithStaticFieldIO.o) { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 7 - Component with default parameter value, read from static function
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp7(int p = OOTypeWithStaticFunctionIO.i()) { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 8 - Component with default parameter value, read from static function
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp8(int p = OOTypeWithStaticFunctionIO.o()) { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 9 - Inner component with default parameter value, read from literal
    "component Comp9 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  component Inner1(int p = 1) { } " +
      "  component Inner2(int p1 = 1, int p2 = 2) { } " +
      "}",
    // 10 - Inner component with default parameter value, read from parameter
    "component Comp10(int p) { " +
      "  port in int i; " +
      "  port out int o; " +
      "  component Inner(int v = p) { } " +
      "}",
    // 11 - Inner component with default parameter value, read from static field
    "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp11 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  component Inner1(int p = OOTypeWithStaticFieldIO.i) { } " +
      "  component Inner2(int p = OOTypeWithStaticFieldIO.o) { } " +
      "}",
    // 12 - Inner component with default parameter value, read from static function
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp12 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  component Inner1(int p = OOTypeWithStaticFunctionIO.i()) { } " +
      "  component Inner2(int p = OOTypeWithStaticFunctionIO.o()) { } " +
      "}",
    // 13 - Inner component with default parameter value, parameter shadows port, read from literal
    "component Comp13 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  component Inner1(int i = 1) { } " +
      "  component Inner2(int i = 1, int o = 2) { } " +
      "}",
    // 14 Inner component with default parameter value, parameter shadows port, read from parameter
    "component Comp14(int p) { " +
      "  port in int i; " +
      "  port out int o; " +
      "  component Inner1(int i = p) { } " +
      "  component Inner1(int o = p) { } " +
      "}",
    // 15 - Inner component with default parameter value, parameter shadows port, read from static field
    "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp15 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  component Inner1(int p = OOTypeWithStaticFieldIO.i) { } " +
      "  component Inner2(int p = OOTypeWithStaticFieldIO.o) { } " +
      "}",
    // 16 - Inner component with default parameter value, parameter shadows port, read from static function
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp16 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  component Inner1(int p = OOTypeWithStaticFunctionIO.i()) { } " +
      "  component Inner2(int p = OOTypeWithStaticFunctionIO.o()) { } " +
      "}",
    // 17 - Inner component with parameter, parameter shadows port, subcomponent instantiation with literal argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "component Comp17 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  component Inner1(int i) { " +
      "    ComponentTypeWithIntParameter sub(i); " +
      "  }" +
      "  component Inner2(int o) { " +
      "    ComponentTypeWithIntParameter sub(o); " +
      "  }" +
      "  component Inner3(int i, int o) { " +
      "    ComponentTypeWithIntParameter sub1(i); " +
      "    ComponentTypeWithIntParameter sub2(o); " +
      "  }" +
      "}"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoPortInDefaultParameterValue());

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
    checker.addCoCo(new NoPortInDefaultParameterValue());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .as(() -> "Findings: " + Log.getFindings().toString())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // 1 - No input port in default parameter value
      arg("component Comp1(int p = i) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 2 - No output port in default parameter value
      arg("component Comp2(int p = o) { " +
          "  port out int o; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 3 - No port in assignment in default parameter value
      arg("component Comp3(int p = i = 1) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 4 - No port in inc prefix expression in default parameter value
      arg("component Comp4(int p = ++i) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 5 - No port in dec prefix expression in default parameter value
      arg("component Comp5(int p = --i) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 6 - No port in inc suffix expression in default parameter value
      arg("component Comp6(int p = i++) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 7 - No port in dec suffix expression in default parameter value
      arg("component Comp7(int p = i--) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 8 - No port in boolean not expression in default parameter value
      arg("component Comp8(boolean p = ~i) { " +
          "  port in boolean i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 9 - No port in logical not expression in default parameter value
      arg("component Comp9(boolean p = !i) { " +
          "  port in boolean i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 10 - No port in multiply expressions (left) in default parameter value
      arg("component Comp10(int p = i * 2) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 11 - No port in multiply expressions (right) in default parameter value
      arg("component Comp11(int p = 2 * i) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 12 - No port in multiply expressions (both) in default parameter value
      arg("component Comp12(int p = i * i) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 13 - No input port in infix expressions in default parameter value
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp13(int p1 = 2 / i2, int p2 = i2 / 2, " +
          "                 int p3 = 2 % i2, int p4 = i2 % 2, " +
          "                 int p5 = 2 + i2, int p6 = i2 + 2, " +
          "                 int p7 = 2 - i2, int p8 = i2 - 2, " +
          "                 boolean p9  = i2 <= 2, boolean p10 = 2 <= i2, " +
          "                 boolean p11 = i2 >= 2, boolean p12 = 2 >= i2, " +
          "                 boolean p13 = i2 < 2,  boolean p14 = 2 < i2,  " +
          "                 boolean p15 = i2 > 2,  boolean p16 = 2 > i2,  " +
          "                 boolean p17 = i2 == 2, boolean p18 = 2 == i2, " +
          "                 boolean p19 = i2 != 2, boolean p20 = 2 != i2, " +
          "                 boolean p21 = i4 && 2, boolean p22 = 2 && i4, " +
          "                 boolean p23 = i4 || 2, boolean p24 = 2 || i4) { " +
          "  port in int i1, i2; " +
          "  port in boolean i3, i4; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 14 - No input port in conditional expressions (condition) in default parameter value
      arg("component Comp14(int p = i ? -2 : 2) { " +
          "  port in boolean i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 15 - No input port in conditional expressions (then) in default parameter value
      arg("component Comp15(int p = true ? i : 2) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 16 - No input port in conditional expressions (else) in default parameter value
      arg("component Comp16(int p = true ? -2 : i) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 17 - No input port in bracket expressions in default parameter value
      arg("component Comp17(int p = (i)) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 18 - No input port in shift expressions in default parameter value
      arg("component Comp18(int p1 = i << 1, int p2 = 1 << i, " +
          "                 int p3 = i >> 1, int p4 = 1 >> i, " +
          "                 int p5 = i >>> 1, int p6 = 1 >>> i) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 19 - No input port in binary expressions in default parameter value
      arg("component Comp19(boolean p1 = i & true, boolean p2 = true & i, " +
          "                 boolean p3 = i ^ true, boolean p4 = true ^ i, " +
          "                 boolean p5 = i | true, boolean p6 = true | i) { " +
          "  port in boolean i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 20 - No input port in method call argument in default parameter value
      arg("import montiarc.test.FunctionWithIntParameter; " +
          "component Comp20(int v = FunctionWithIntParameter(i)) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 21 - No input port in default parameter value of inner component
      arg("component Comp21 { " +
          "  component Inner(int p = i) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 22 - No output port in default parameter value of inner component
      arg("component Comp22 { " +
          "  port out int o; " +
          "  component Inner(int p = o) { " +
          "    port out int o; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 23 - No port in assignment in default parameter value of inner component
      arg("component Comp23 { " +
          "  component Inner(int p = i = 1) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 24 - No port in inc prefix expression in default parameter value of inner component
      arg("component Comp24 { " +
          "  component Inner(int p = ++i) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 25 - No port in dec prefix expression in default parameter value of inner component
      arg("component Comp25 { " +
          "  component Inner(int p = --i) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 26 - No port in inc suffix expression in default parameter value of inner component
      arg("component Comp26 { " +
          "  component Inner(int p = i++) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 27 - No port in dec suffix expression in default parameter value of inner component
      arg("component Comp27 { " +
          "  component Inner(int p = i--) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 28 - No port in boolean not expression in default parameter value of inner component
      arg("component Comp28{ " +
          "  component Inner(boolean p = ~i) { " +
          "    port in boolean i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 29 - No port in logical not expression in default parameter value of inner component
      arg("component Comp29 { " +
          "  component Inner(boolean p = !i) { " +
          "    port in boolean i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 30 - No port in multiply expressions (left) in default parameter value of inner component
      arg("component Comp30 { " +
          "  component Inner(int p = i * 2) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 31 - No port in multiply expressions (right) in default parameter value of inner component
      arg("component Comp31 { " +
          "  component Inner(int p = 2 * i) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 32 - No port in multiply expressions (both) in default parameter value of inner component
      arg("component Comp32 { " +
          "  component Inner(int p = i * i) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 33 - No input port in infix expressions in default parameter value of inner component
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp33 { " +
          "  component Inner (int p1 = 2 / i2, int p2 = i2 / 2, " +
          "                   int p3 = 2 % i2, int p4 = i2 % 2, " +
          "                   int p5 = 2 + i2, int p6 = i2 + 2, " +
          "                   int p7 = 2 - i2, int p8 = i2 - 2, " +
          "                   boolean p9  = i2 <= 2, boolean p10 = 2 <= i2, " +
          "                   boolean p11 = i2 >= 2, boolean p12 = 2 >= i2, " +
          "                   boolean p13 = i2 < 2,  boolean p14 = 2 < i2,  " +
          "                   boolean p15 = i2 > 2,  boolean p16 = 2 > i2,  " +
          "                   boolean p17 = i2 == 2, boolean p18 = 2 == i2, " +
          "                   boolean p19 = i2 != 2, boolean p20 = 2 != i2, " +
          "                   boolean p21 = i4 && 2, boolean p22 = 2 && i4, " +
          "                   boolean p23 = i4 || 2, boolean p24 = 2 || i4) { " +
          "    port in int i1, i2; " +
          "    port in boolean i3, i4; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 34 - No input port in conditional expressions (condition) in default parameter value of inner component
      arg("component Comp34 { " +
          "  component Inner(int p = i ? -2 : 2) { " +
          "    port in boolean i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 35 - No input port in conditional expressions (then) in default parameter value of inner component
      arg("component Comp35 { " +
          "  component Inner(int p = true ? i : 2) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 36 - No input port in conditional expressions (else) in default parameter value of inner component
      arg("component Comp36 { " +
          "  component Inner(int p = true ? -2 : i) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 37 - No input port in bracket expressions in default parameter value of inner component
      arg("component Comp37 { " +
          "  component Inner(int p = (i)) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 38 - No input port in shift expressions in default parameter value of inner component
      arg("component Comp38 { " +
          "  component Inner(int p1 = i << 1, int p2 = 1 << i, " +
          "                  int p3 = i >> 1, int p4 = 1 >> i, " +
          "                  int p5 = i >>> 1, int p6 = 1 >>> i) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 39 - No input port in binary expressions in default parameter value of inner component
      arg("component Comp39 { " +
          "  component Inner(boolean p1 = i & true, boolean p2 = true & i, " +
          "                 boolean p3 = i ^ true, boolean p4 = true ^ i, " +
          "                 boolean p5 = i | true, boolean p6 = true | i) { " +
          "    port in boolean i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 40 - No input port in method call argument in default parameter value of inner component
      arg("import montiarc.test.FunctionWithIntParameter; " +
          "component Comp40 { " +
          "  component Inner(int v = FunctionWithIntParameter(i)) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
