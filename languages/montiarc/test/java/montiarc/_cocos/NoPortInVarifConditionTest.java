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

/**
 * The class under test is {@link NoPortInVarIfCondition}.
 */
class NoPortInVarifConditionTest extends MontiArcTestBase {

  final static String SYMBOLS_DIR = "symbols";

  @BeforeEach
  protected void setUp() {
    MontiArcMill.globalScope().setSymbolPath(
      new MCPath(Paths.get(TEST_RESOURCE, SYMBOLS_DIR))
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no ports, no varif
    "component ValidComp1 { }",
    // ports, no varif
    "component ValidComp2 { port in int i; port out int o; }",
    // varif condition with a literal
    "component ValidComp3 { port in int i; port out int o; varif(true) { } }",
    // varif condition reading a value from a parameter
    "component ValidComp4(int p) { port in int i; port out int o; varif(p > 1) { } }",
    // varif condition reading values from a parameter's fields
    "import montiarc.test.OOTypeWithFieldIO; component ValidComp5(OOTypeWithFieldIO p) { port in int i; port out int o; varif(p.i > 1) { } varif(p.o > 1) { } }",
    // varif condition reading values from a parameter's methods
    "import montiarc.test.OOTypeWithFunctionIO; component ValidComp6(OOTypeWithFunctionIO p) { port in int i; port out int o; varif(p.i() > 1) { } varif(p.o() > 1) { } }",
    // varif condition reading values from an external type's static fields
    "import montiarc.test.OOTypeWithStaticFieldIO; component ValidComp7 { port in int i; port out int o; varif(OOTypeWithStaticFieldIO.i > 1) { } varif(OOTypeWithStaticFieldIO.o > 1) { } }",
    // varif condition reading values from an external type's static functions
    "import montiarc.test.OOTypeWithStaticFunctionIO; component ValidComp8 { port in int i; port out int o; varif(OOTypeWithStaticFunctionIO.i() > 1) { } varif(OOTypeWithStaticFunctionIO.o() > 1) { } }",
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
      // input port referenced directly in varif condition
      arg("component InvalidComp1 { port in int i; varif(i) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // output port referenced directly in varif condition
      arg("component InvalidComp2 { port out int o; varif(o) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in an assignment expression in varif condition
      arg("component InvalidComp3 { port out boolean o; varif(o = true) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in a prefix increment expression in varif condition
      arg("component InvalidComp4 { port in int i; varif(2 == ++i) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in a prefix decrement expression in varif condition
      arg("component InvalidComp5 { port in int i; varif(2 == --i) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in a suffix increment expression in varif condition
      arg("component InvalidComp6 { port in int i; varif(2 == i++) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in a suffix decrement expression in varif condition
      arg("component InvalidComp7 { port in int i; varif(2 == i--) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in a boolean-not expression in varif condition
      arg("component InvalidComp8 { port in boolean i; varif(~i) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in a logical-not expression in varif condition
      arg("component InvalidComp9 { port in boolean i; varif(!i) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced on the left of a multiplication in varif condition
      arg("component InvalidComp10 { port in int i; varif(2 == i * 2) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced on the right of a multiplication in varif condition
      arg("component InvalidComp11 { port in int i; varif(2 == 2 * i) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced on both sides of a multiplication in varif condition
      arg("component InvalidComp12 { port in int i; varif(2 == i * i) { } }",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced on either side of every infix operator in varif condition
      arg("""
        component InvalidComp13 {
          port in int i1, i2;
          port in boolean i3, i4;
          varif(2 == 2 / i2) { } varif(2 == i2 / 2) { }
          varif(2 == 2 % i2) { } varif(2 == i2 % 2) { }
          varif(2 == 2 + i2) { } varif(2 == i2 + 2) { }
          varif(2 == 2 - i2) { } varif(2 == i2 - 2) { }
          varif(i2 <= 2) { } varif(2 <= i2) { }
          varif(i2 >= 2) { } varif(2 >= i2) { }
          varif(i2 < 2) { } varif(2 < i2) { }
          varif(i2 > 2) { } varif(2 > i2) { }
          varif(i2 == 2) { } varif(2 == i2) { }
          varif(i2 != 2) { } varif(2 != i2) { }
          varif(i4 && 2) { } varif(2 && i4) { }
          varif(i4 || 2) { } varif(2 || i4) { }
        }
        """,
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
      // port referenced in the condition of a conditional expression in varif condition
      arg("component InvalidComp14 { port in boolean i; varif(i ? true : false) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in the then-branch of a conditional expression in varif condition
      arg("component InvalidComp15 { port in boolean i; varif(true ? i : false) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in the else-branch of a conditional expression in varif condition
      arg("component InvalidComp16 { port in boolean i; varif(true ? true : i) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in a bracket expression in varif condition
      arg("component InvalidComp17 { port in boolean i; varif((i)) { } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced on either side of every shift operator in varif condition
      arg("""
        component InvalidComp18 {
          port in int i;
          varif(2 == i << 1) { }
          varif(2 == 1 << i) { }
          varif(2 == i >> 1) { }
          varif(2 == 1 >> i) { }
          varif(2 == i >>> 1) { }
          varif(2 == 1 >>> i) { }
        }
        """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced on either side of every binary bitwise/boolean operator in varif condition
      arg("""
        component InvalidComp19 {
          port in boolean i;
          varif(i & true) { }
          varif(true & i) { }
          varif(i ^ true) { }
          varif(true ^ i) { }
          varif(i | true) { }
          varif(true | i) { }
        }
        """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced as a method call argument in varif condition
      arg("""
        import montiarc.test.FunctionWithBooleanParameter;
        component InvalidComp20 {
          port in boolean i;
          varif(FunctionWithBooleanParameter(i)) { }
        }
        """,
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
