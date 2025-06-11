/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.NoPortInSuperComponentArgument;
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

class NoPortInSuperComponentArgumentTest extends MontiArcTestBase {

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
    // 3 - Subcomponent instantiation
    "import montiarc.test.ComponentType; " +
      "component Comp4 extends ComponentType { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 4 - Subcomponent instantiation with literal argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "component Comp4 extends ComponentTypeWithIntParameter(1) { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 5 - Subcomponent instantiation with parameter argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "component Comp5(int p) extends ComponentTypeWithIntParameter(p) { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 6 - Subcomponent instantiation with oo-type with field parameter argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp6(OOTypeWithFieldIO p) extends ComponentTypeWithIntParameter(p.i) { " +
      "  port in int i; " +
      "}",
    // 7 - Subcomponent instantiation with oo-type with field parameter argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp7(OOTypeWithFieldIO p) extends ComponentTypeWithIntParameter(p.o) { " +
      "  port out int o; " +
      "}",
    // 9 - Subcomponent instantiation with oo-type with function parameter argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp9(OOTypeWithFunctionIO p) extends ComponentTypeWithIntParameter(p.i()) { " +
      "  port in int i; " +
      "}",
    // 10 - Subcomponent instantiation with oo-type with function parameter argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp19(OOTypeWithFunctionIO p) extends ComponentTypeWithIntParameter(p.o()) { " +
      "  port out int o; " +
      "}",
    // 11 - Subcomponent instantiation with static field argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp11 extends ComponentTypeWithIntParameter(OOTypeWithStaticFieldIO.i) { " +
      "  port in int i; " +
      "}",
    // 12 - Subcomponent instantiation with static field argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp11 extends ComponentTypeWithIntParameter(OOTypeWithStaticFieldIO.o) { " +
      "  port out int o; " +
      "}",
    // 13 - Subcomponent instantiation with static function argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp13 extends ComponentTypeWithIntParameter(OOTypeWithStaticFunctionIO.i()) { " +
      "  port in int i; " +
      "}",
    // 14 - Subcomponent instantiation with static function argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp13 extends ComponentTypeWithIntParameter(OOTypeWithStaticFunctionIO.o()) { " +
      "  port out int o; " +
      "}",
    // 15 - Subcomponent instantiation with named parameter and literal argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "component Comp15 extends ComponentTypeWithIntIOParameters(i = 1, o = 1){ " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 16 - Subcomponent instantiation with named parameter and parameter argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "component Comp16(int p) extends ComponentTypeWithIntIOParameters(i = p, o = p) { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 17 - Subcomponent instantiation with named parameter and oo-type with field parameter argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp17(OOTypeWithFieldIO p) extends ComponentTypeWithIntIOParameters(i = p.i, o = p.o) { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 18 - Subcomponent instantiation with named parameter and oo-type with function parameter argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp18(OOTypeWithFunctionIO p) extends ComponentTypeWithIntIOParameters(i = p.i(), o = p.o()) { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 19 - Subcomponent instantiation with named parameter and static field argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp19 extends ComponentTypeWithIntIOParameters(i = OOTypeWithStaticFieldIO.i, " +
      "                                                          o = OOTypeWithStaticFieldIO.o) { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 20 - Subcomponent instantiation with named parameter and static function argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp20 extends ComponentTypeWithIntIOParameters(i = OOTypeWithStaticFunctionIO.i(), " +
      "                                                          o = OOTypeWithStaticFunctionIO.o()) { " +
      "  port in int i; " +
      "  port out int o; " +
      "}"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoPortInSuperComponentArgument());

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
    checker.addCoCo(new NoPortInSuperComponentArgument());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .as(() -> "Findings: " + Log.getFindings().toString())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // 1 - No input port in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp1 extends ComponentTypeWithIntParameter(i) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 2 - No output port in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp2 extends ComponentTypeWithIntParameter(o) { " +
          "  port out int o; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 3 - No port in assignment in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp3 extends ComponentTypeWithIntParameter((o = 1)) { " +
          "  port out int o; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 4 - No port in inc prefix expression in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp4 extends ComponentTypeWithIntParameter(++i) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 5 - No port in dec prefix expression in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp5 extends ComponentTypeWithIntParameter(--i) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 6 - No port in inc suffix expression in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp6 extends ComponentTypeWithIntParameter(i++) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 7 - No port in dec suffix expression in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp7 extends ComponentTypeWithIntParameter(i--) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 8 - No port in boolean not expression in super component argument
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp8 extends ComponentTypeWithBooleanParameter(~i) { " +
          "  port in boolean i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 9 - No port in logical not expression in super component argument
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp9 extends ComponentTypeWithBooleanParameter(!i) { " +
          "  port in boolean i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 10 - No port in multiply expressions (left) in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp10 extends ComponentTypeWithIntParameter(i * 2) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 11 - No port in multiply expressions (right) in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp11 extends ComponentTypeWithIntParameter(2 * i) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 12 - No port in multiply expressions (both) in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp12 extends ComponentTypeWithIntParameter(i * i) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 13 - No input port in infix expressions in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp13 extends ComponentTypeWithIntParameter(2 / i2), " +
          "                         ComponentTypeWithIntParameter(i2 / 2), " +
          "                         ComponentTypeWithIntParameter(2 % i2), " +
          "                         ComponentTypeWithIntParameter(i2 % 2), " +
          "                         ComponentTypeWithIntParameter(2 + i2), " +
          "                         ComponentTypeWithIntParameter(i2 + 2), " +
          "                         ComponentTypeWithIntParameter(2 - i2), " +
          "                         ComponentTypeWithIntParameter(i2 - 2), " +
          "                         ComponentTypeWithBooleanParameter(i2 <= 2), " +
          "                         ComponentTypeWithBooleanParameter(2 <= i2), " +
          "                         ComponentTypeWithBooleanParameter(i2 >= 2), " +
          "                         ComponentTypeWithBooleanParameter(2 >= i2), " +
          "                         ComponentTypeWithBooleanParameter(i2 < 2), " +
          "                         ComponentTypeWithBooleanParameter(2 < i2), " +
          "                         ComponentTypeWithBooleanParameter(i2 > 2), " +
          "                         ComponentTypeWithBooleanParameter(2 > i2), " +
          "                         ComponentTypeWithBooleanParameter(i2 == 2), " +
          "                         ComponentTypeWithBooleanParameter(2 == i2), " +
          "                         ComponentTypeWithBooleanParameter(i2 != 2), " +
          "                         ComponentTypeWithBooleanParameter(2 != i2), " +
          "                         ComponentTypeWithBooleanParameter(i4 && 2), " +
          "                         ComponentTypeWithBooleanParameter(2 && i4), " +
          "                         ComponentTypeWithBooleanParameter(i4 || 2), " +
          "                         ComponentTypeWithBooleanParameter(2 || i4) { " +
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
      // 14 - No input port in conditional expressions (condition) in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp14 extends ComponentTypeWithIntParameter(i ? -2 : 2) { " +
          "  port in boolean i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 15 - No input port in conditional expressions (then) in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp15 extends ComponentTypeWithIntParameter(true ? i : 2) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 16 - No input port in conditional expressions (else) in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp16 extends ComponentTypeWithIntParameter(true ? -2 : i) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 17 - No input port in bracket expressions in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp17 extends ComponentTypeWithIntParameter((i)) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 18 - No input port in shift expressions in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp18 extends ComponentTypeWithIntParameter(i << 1), " +
          "                         ComponentTypeWithIntParameter(1 << i), " +
          "                         ComponentTypeWithIntParameter(i >> 1), " +
          "                         ComponentTypeWithIntParameter(1 >> i), " +
          "                         ComponentTypeWithIntParameter(i >>> 1), " +
          "                         ComponentTypeWithIntParameter(1 >>> i) { " +
          "  port in int i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 19 - No input port in binary expressions in super component argument
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp19 extends ComponentTypeWithBooleanParameter(i & true), " +
          "                         ComponentTypeWithBooleanParameter(true & i), " +
          "                         ComponentTypeWithBooleanParameter(i ^ true), " +
          "                         ComponentTypeWithBooleanParameter(true ^ i), " +
          "                         ComponentTypeWithBooleanParameter(i | true), " +
          "                         ComponentTypeWithBooleanParameter(true | i) { " +
          "  port in boolean i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 20 - No input port in method call argument in super component argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "import montiarc.test.FunctionWithIntParameter; " +
          "component Comp20 extends ComponentTypeWithIntParameter(FunctionWithIntParameter(i)) { " +
          "  port in boolean i; " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 21 - No input port in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp21 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = i) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 22 - No output port in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp22 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = o) { " +
          "    port out int o; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 23 - No port in assignment in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp23 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = i = 1) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 24 - No port in inc prefix expression in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp24 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = ++i) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 25 - No port in dec prefix expression in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp25 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = --i) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 26 - No port in inc suffix expression in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp26 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = i++) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 27 - No port in dec suffix expression in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp27 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = i--) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 28 - No port in boolean not expression in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp28 { " +
          "  component Inner extends ComponentTypeWithBooleanParameter(p = ~i) { " +
          "    port in boolean i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 29 - No port in logical not expression in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp29 { " +
          "  component Inner extends ComponentTypeWithBooleanParameter(p = !i) { " +
          "    port in boolean i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 30 - No port in multiply expressions (left) in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp30 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = i * 2) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 31 - No port in multiply expressions (right) in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp31 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = 2 * i) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 32 - No port in multiply expressions (both) in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp32 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = i * i) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 33 - No input port in infix expressions in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp33 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = 2 / i2),  " +
          "                          ComponentTypeWithIntParameter(p = i2 / 2), " +
          "                          ComponentTypeWithIntParameter(p = 2 % i2), " +
          "                          ComponentTypeWithIntParameter(p = i2 % 2), " +
          "                          ComponentTypeWithIntParameter(p = 2 + i2), " +
          "                          ComponentTypeWithIntParameter(p = i2 + 2), " +
          "                          ComponentTypeWithIntParameter(p = 2 - i2), " +
          "                          ComponentTypeWithIntParameter(p = i2 - 2), " +
          "                          ComponentTypeWithBooleanParameter(p = i2 <= 2), " +
          "                          ComponentTypeWithBooleanParameter(p = 2 <= i2), " +
          "                          ComponentTypeWithBooleanParameter(p = i2 >= 2), " +
          "                          ComponentTypeWithBooleanParameter(p = 2 >= i2), " +
          "                          ComponentTypeWithBooleanParameter(p = i2 < 2), " +
          "                          ComponentTypeWithBooleanParameter(p = 2 < i2), " +
          "                          ComponentTypeWithBooleanParameter(p = i2 > 2), " +
          "                          ComponentTypeWithBooleanParameter(p = 2 > i2), " +
          "                          ComponentTypeWithBooleanParameter(p = i2 == 2), " +
          "                          ComponentTypeWithBooleanParameter(p = 2 == i2), " +
          "                          ComponentTypeWithBooleanParameter(p = i2 != 2), " +
          "                          ComponentTypeWithBooleanParameter(p = 2 != i2), " +
          "                          ComponentTypeWithBooleanParameter(p = i4 && 2), " +
          "                          ComponentTypeWithBooleanParameter(p = 2 && i4), " +
          "                          ComponentTypeWithBooleanParameter(p = i4 || 2), " +
          "                          ComponentTypeWithBooleanParameter(p = 2 || i4) { " +
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
      // 34 - No input port in conditional expressions (condition) in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp34 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = i ? -2 : 2) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 35 - No input port in conditional expressions (then) in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp35 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = true ? i : 2) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 36 - No input port in conditional expressions (else) in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp36 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = true ? -2 : i) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 37 - No input port in bracket expressions in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp37 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = (i)) { " +
          "    port in int i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 38 - No input port in shift expressions in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp38 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = i << 1), " +
          "                          ComponentTypeWithIntParameter(p = 1 << i), " +
          "                          ComponentTypeWithIntParameter(p = i >> 1), " +
          "                          ComponentTypeWithIntParameter(p = 1 >> i), " +
          "                          ComponentTypeWithIntParameter(p = i >>> 1), " +
          "                          ComponentTypeWithIntParameter(p = 1 >>> i) { " +
          "    port in int i; " +
          "  }" +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 39 - No input port in binary expressions in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp39 { " +
          "  component Inner extends ComponentTypeWithBooleanParameter(p = i & true), " +
          "                          ComponentTypeWithBooleanParameter(p = true & i), " +
          "                          ComponentTypeWithBooleanParameter(p = i ^ true), " +
          "                          ComponentTypeWithBooleanParameter(p = true ^ i), " +
          "                          ComponentTypeWithBooleanParameter(p = i | true), " +
          "                          ComponentTypeWithBooleanParameter(p = true | i) { " +
          "    port in boolean i; " +
          "  }" +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 40 - No input port in method call argument in super component argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "import montiarc.test.FunctionWithIntParameter; " +
          "component Comp40 { " +
          "  component Inner extends ComponentTypeWithIntParameter(p = FunctionWithIntParameter(i)) { " +
          "    port in boolean i; " +
          "  } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
