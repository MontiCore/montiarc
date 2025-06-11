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
import variablearc._cocos.NoPortInVarIfCondition;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

class NoPortInVarifConditionTest extends MontiArcTestBase {

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
    // 3 - Varif condition with literal
    "component Comp3 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  varif(true) { } " +
      "}",
    // 4 - Varif condition, read value from parameter
    "component Comp4(int p) { " +
      "  port in int i; " +
      "  port out int o; " +
      "  varif(p > 1) { } " +
      "}",
    // 5 - Varif condition, read value from field
    "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp5(OOTypeWithFieldIO p) { " +
      "  port in int i; " +
      "  port out int o; " +
      "  varif(p.i > 1) { } " +
      "  varif(p.o > 1) { } " +
      "}",
    // 6 - Varif condition, read value from function
    "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp6(OOTypeWithFunctionIO p) { " +
      "  port in int i; " +
      "  port out int o; " +
      "  varif(p.i() > 1) { } " +
      "  varif(p.o() > 1) { } " +
      "}",
    // 7 - Varif condition, read value from static field
    "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp7 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  varif(OOTypeWithStaticFieldIO.i > 1) { } " +
      "  varif(OOTypeWithStaticFieldIO.o > 1) { } " +
      "}",
    // 8 - Varif condition, read value from static function
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp8 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  varif(OOTypeWithStaticFieldIO.i() > 1) { } " +
      "  varif(OOTypeWithStaticFieldIO.o() > 1) { } " +
      "}",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoPortInVarIfCondition());

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
    checker.addCoCo(new NoPortInVarIfCondition());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .as(() -> "Findings: " + Log.getFindings().toString())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // 1 - No input port in varif condition
      arg("component Comp1 { " +
          "  port in int i; " +
          "  varif(i) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 2 - No output port in varif condition
      arg("component Comp2 { " +
          "  port out int o; " +
          "  varif(o) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 3 - No port in assignment in varif condition
      arg("component Comp3 { " +
          "  port out boolean o; " +
          "  varif(o = true) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 4 - No port in inc prefix expression in varif condition
      arg("component Comp4 { " +
          "  port in int i; " +
          "  varif(2 == ++i) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 5 - No port in dec prefix expression in varif condition
      arg("component Comp5 { " +
          "  port in int i; " +
          "  varif(2 == --i) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 6 - No port in inc suffix expression in varif condition
      arg("component Comp6 { " +
          "  port in int i; " +
          "  varif(2 == i++) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 7 - No port in dec suffix expression in varif condition
      arg("component Comp7 { " +
          "  port in int i; " +
          "  varif(2 == i--) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 8 - No port in boolean not expression in varif condition
      arg("component Comp8 { " +
          "  port in boolean i; " +
          "  varif(~i) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 9 - No port in logical not expression in varif condition
      arg("component Comp9 { " +
          "  port in boolean i; " +
          "  varif(!i) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 10 - No port in multiply expressions (left) in varif condition
      arg("component Comp10 { " +
          "  port in int i; " +
          "  varif(2 == i * 2) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 11 - No port in multiply expressions (right) in varif condition
      arg("component Comp11 { " +
          "  port in int i; " +
          "  varif(2 == 2 * i) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 12 - No port in multiply expressions (both) in varif condition
      arg("component Comp12 { " +
          "  port in int i; " +
          "  varif(2 == i * i) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 13 - No input port in infix expressions in varif condition
      arg("component Comp13 { " +
          "  port in int i1, i2; " +
          "  port in boolean i3, i4; " +
          "  varif(2 == 2 / i2) { } varif(2 == i2 / 2) { } " +
          "  varif(2 == 2 % i2) { } varif(2 == i2 % 2) { } " +
          "  varif(2 == 2 + i2) { } varif(2 == i2 + 2) { } " +
          "  varif(2 == 2 - i2) { } varif(2 == i2 - 2) { } " +
          "  varif(i2 <= 2) { } varif(2 <= i2) { } " +
          "  varif(i2 >= 2) { } varif(2 >= i2) { } " +
          "  varif(i2 < 2) { } varif(2 < i2) { } " +
          "  varif(i2 > 2) { } varif(2 > i2) { } " +
          "  varif(i2 == 2) { } varif(2 == i2) { } " +
          "  varif(i2 != 2) { } varif(2 != i2) { } " +
          "  varif(i4 && 2) { } varif(2 && i4) { } " +
          "  varif(i4 || 2) { } varif(2 || i4) { } " +
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
      // 14 - No input port in conditional expressions (condition) in varif condition
      arg("component Comp14 { " +
          "  port in boolean i; " +
          "  varif(i ? true : false) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 15 - No input port in conditional expressions (then) in varif condition
      arg("component Comp15 { " +
          "  port in boolean i; " +
          "  varif(true ? i : false) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 16 - No input port in conditional expressions (else) in varif condition
      arg("component Comp16 { " +
          "  port in boolean i; " +
          "  varif(true ? true : i) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 17 - No input port in bracket expressions in varif condition
      arg("component Comp17 { " +
          "  port in boolean i; " +
          "  varif((i)) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 18 - No input port in shift expressions in varif condition
      arg("component Comp18 { " +
          "  port in int i; " +
          "  varif(2 == i << 1) { } " +
          "  varif(2 == 1 << i) { } " +
          "  varif(2 == i >> 1) { } " +
          "  varif(2 == 1 >> i) { } " +
          "  varif(2 == i >>> 1) { } " +
          "  varif(2 == 1 >>> i) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 19 - No input port in binary expressions in varif condition
      arg("component Comp19 { " +
          "  port in boolean i; " +
          "  varif(i & true) { } " +
          "  varif(true & i) { } " +
          "  varif(i ^ true) { } " +
          "  varif(true ^ i) { } " +
          "  varif(i | true) { } " +
          "  varif(true | i) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 20 - No input port in method call argument in varif condition
      arg("import montiarc.test.FunctionWithBooleanParameter; " +
          "component Comp20 { " +
          "  port in boolean i; " +
          "  varif(FunctionWithBooleanParameter(i)) { } " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
