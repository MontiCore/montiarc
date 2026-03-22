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

import java.nio.file.Paths;
import java.util.stream.Stream;

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoPortInDefaultParameterValue}.
 */
class NoPortInDefaultParameterValueTest extends MontiArcTestBase {

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
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // no ports, no expressions
      arg("""
        component ValidComp1 { }
        """
      ),
      // no expressions
      arg("""
        component ValidComp2 {
          port in int i;
          port out int o;
        }
        """
      ),
      // component with default parameter value, read from literal
      arg("""
        component ValidComp3(int p = 1) {
          port in int i;
          port out int o;
        }
        """
      ),
      // component with default parameter value (two parameters), read from literal
      arg("""
        component ValidComp4(int p1 = 1, int p2 = 2) {
          port in int i;
          port out int o;
        }
        """
      ),
      // component with default parameter value, read from static field
      arg("""
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp5(int p = OOTypeWithStaticFieldIO.i) {
          port in int i;
          port out int o;
        }
        """
      ),
      // component with default parameter value, read from static field
      arg("""
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp6(int p = OOTypeWithStaticFieldIO.o) {
          port in int i;
          port out int o;
        }
        """
      ),
      // component with default parameter value, read from static function
      arg("""
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp7(int p = OOTypeWithStaticFunctionIO.i()) {
          port in int i;
          port out int o;
        }
        """
      ),
      // component with default parameter value, read from static function
      arg("""
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp8(int p = OOTypeWithStaticFunctionIO.o()) {
          port in int i;
          port out int o;
        }
        """
      ),
      // inner component with default parameter value, read from literal
      arg("""
        component ValidComp9 {
          port in int i;
          port out int o;
          component Inner1(int p = 1) { }
          component Inner2(int p1 = 1, int p2 = 2) { }
        }
        """
      ),
      // inner component with default parameter value, read from parameter
      arg("""
        component ValidComp10(int p) {
          port in int i;
          port out int o;
          component Inner(int v = p) { }
        }
        """
      ),
      // inner component with default parameter value, read from static field
      arg("""
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp11 {
          port in int i;
          port out int o;
          component Inner1(int p = OOTypeWithStaticFieldIO.i) { }
          component Inner2(int p = OOTypeWithStaticFieldIO.o) { }
        }
        """
      ),
      // inner component with default parameter value, read from static function
      arg("""
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp12 {
          port in int i;
          port out int o;
          component Inner1(int p = OOTypeWithStaticFunctionIO.i()) { }
          component Inner2(int p = OOTypeWithStaticFunctionIO.o()) { }
        }
        """),
      // inner component with default parameter value, parameter shadows port, read from literal
      arg("""
        component ValidComp13 {
          port in int i;
          port out int o;
          component Inner1(int i = 1) { }
          component Inner2(int i = 1, int o = 2) { }
        }
        """
      ),
      // inner component with default parameter value, parameter shadows port, read from parameter
      arg("""
        component ValidComp14(int p) {
          port in int i;
          port out int o;
          component Inner1(int i = p) { }
          component Inner1(int o = p) { }
        }
        """
      ),
      // inner component with default parameter value, parameter shadows port, read from static field
      arg("""
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp15 {
          port in int i;
          port out int o;
          component Inner1(int p = OOTypeWithStaticFieldIO.i) { }
          component Inner2(int p = OOTypeWithStaticFieldIO.o) { }
        }
        """
      ),
      // inner component with default parameter value, parameter shadows port, read from static function
      arg("""
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp16 {
          port in int i;
          port out int o;
          component Inner1(int p = OOTypeWithStaticFunctionIO.i()) { }
          component Inner2(int p = OOTypeWithStaticFunctionIO.o()) { }
        }
        """
      ),
      // inner component with parameter, parameter shadows port, subcomponent instantiation with literal argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidComp17 {
          port in int i;
          port out int o;
          component Inner1(int i) {
            ComponentTypeWithIntParameter sub(i);
          }
          component Inner2(int o) {
            ComponentTypeWithIntParameter sub(o);
          }
          component Inner3(int i, int o) {
            ComponentTypeWithIntParameter sub1(i);
            ComponentTypeWithIntParameter sub2(o);
          }
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // input port in default parameter value
      arg("""
          component InvalidComp1(int p = i) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // output port in default parameter value
      arg("""
          component InvalidComp2(int p = o) {
            port out int o;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in assignment in default parameter value
      arg("""
          component InvalidComp3(int p = i = 1) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in inc prefix expression in default parameter value
      arg("""
          component InvalidComp4(int p = ++i) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in dec prefix expression in default parameter value
      arg("""
          component InvalidComp5(int p = --i) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in inc suffix expression in default parameter value
      arg("""
          component InvalidComp6(int p = i++) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in dec suffix expression in default parameter value
      arg("""
          component InvalidComp7(int p = i--) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in boolean not expression in default parameter value
      arg("""
          component InvalidComp8(boolean p = ~i) {
            port in boolean i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in logical not expression in default parameter value
      arg("""
          component InvalidComp9(boolean p = !i) {
            port in boolean i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in multiply expressions (left) in default parameter value
      arg("""
          component InvalidComp10(int p = i * 2) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in multiply expressions (right) in default parameter value
      arg("""
          component InvalidComp11(int p = 2 * i) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in multiply expressions (both) in default parameter value
      arg("""
          component InvalidComp12(int p = i * i) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in infix expressions in default parameter value
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp13(int p1 = 2 / i2, int p2 = i2 / 2,
                           int p3 = 2 % i2, int p4 = i2 % 2,
                           int p5 = 2 + i2, int p6 = i2 + 2,
                           int p7 = 2 - i2, int p8 = i2 - 2,
                           boolean p9  = i2 <= 2, boolean p10 = 2 <= i2,
                           boolean p11 = i2 >= 2, boolean p12 = 2 >= i2,
                           boolean p13 = i2 < 2,  boolean p14 = 2 < i2,
                           boolean p15 = i2 > 2,  boolean p16 = 2 > i2,
                           boolean p17 = i2 == 2, boolean p18 = 2 == i2,
                           boolean p19 = i2 != 2, boolean p20 = 2 != i2,
                           boolean p21 = i4 && 2, boolean p22 = 2 && i4,
                           boolean p23 = i4 || 2, boolean p24 = 2 || i4) {
            port in int i1, i2;
            port in boolean i3, i4;
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
      // input port in conditional expressions (condition) in default parameter value
      arg("""
          component InvalidComp14(int p = i ? -2 : 2) {
            port in boolean i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in conditional expressions (then) in default parameter value
      arg("""
          component InvalidComp15(int p = true ? i : 2) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in conditional expressions (else) in default parameter value
      arg("""
          component InvalidComp16(int p = true ? -2 : i) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in bracket expressions in default parameter value
      arg("""
          component InvalidComp17(int p = (i)) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in shift expressions in default parameter value
      arg("""
          component InvalidComp18(int p1 = i << 1, int p2 = 1 << i,
                           int p3 = i >> 1, int p4 = 1 >> i,
                           int p5 = i >>> 1, int p6 = 1 >>> i) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in binary expressions in default parameter value
      arg("""
          component InvalidComp19(boolean p1 = i & true, boolean p2 = true & i,
                           boolean p3 = i ^ true, boolean p4 = true ^ i,
                           boolean p5 = i | true, boolean p6 = true | i) {
            port in boolean i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in method call argument in default parameter value
      arg("""
          import montiarc.test.FunctionWithIntParameter;
          component InvalidComp20(int v = FunctionWithIntParameter(i)) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in default parameter value of inner component
      arg("""
          component InvalidComp21 {
            component Inner(int p = i) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // output port in default parameter value of inner component
      arg("""
          component InvalidComp22 {
            port out int o;
            component Inner(int p = o) {
              port out int o;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in assignment in default parameter value of inner component
      arg("""
          component InvalidComp23 {
            component Inner(int p = i = 1) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in inc prefix expression in default parameter value of inner component
      arg("""
          component InvalidComp24 {
            component Inner(int p = ++i) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in dec prefix expression in default parameter value of inner component
      arg("""
          component InvalidComp25 {
            component Inner(int p = --i) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in inc suffix expression in default parameter value of inner component
      arg("""
          component InvalidComp26 {
            component Inner(int p = i++) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in dec suffix expression in default parameter value of inner component
      arg("""
          component InvalidComp27 {
            component Inner(int p = i--) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in boolean not expression in default parameter value of inner component
      arg("""
          component InvalidComp28 {
            component Inner(boolean p = ~i) {
              port in boolean i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in logical not expression in default parameter value of inner component
      arg("""
          component InvalidComp29 {
            component Inner(boolean p = !i) {
              port in boolean i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in multiply expressions (left) in default parameter value of inner component
      arg("""
          component InvalidComp30 {
            component Inner(int p = i * 2) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in multiply expressions (right) in default parameter value of inner component
      arg("""
          component InvalidComp31 {
            component Inner(int p = 2 * i) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in multiply expressions (both) in default parameter value of inner component
      arg("""
          component InvalidComp32 {
            component Inner(int p = i * i) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in infix expressions in default parameter value of inner component
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp33 {
            component Inner(int p1 = 2 / i2, int p2 = i2 / 2,
                            int p3 = 2 % i2, int p4 = i2 % 2,
                            int p5 = 2 + i2, int p6 = i2 + 2,
                            int p7 = 2 - i2, int p8 = i2 - 2,
                            boolean p9  = i2 <= 2, boolean p10 = 2 <= i2,
                            boolean p11 = i2 >= 2, boolean p12 = 2 >= i2,
                            boolean p13 = i2 < 2,  boolean p14 = 2 < i2,
                            boolean p15 = i2 > 2,  boolean p16 = 2 > i2,
                            boolean p17 = i2 == 2, boolean p18 = 2 == i2,
                            boolean p19 = i2 != 2, boolean p20 = 2 != i2,
                            boolean p21 = i4 && 2, boolean p22 = 2 && i4,
                            boolean p23 = i4 || 2, boolean p24 = 2 || i4) {
              port in int i1, i2;
              port in boolean i3, i4;
            }
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
      // input port in conditional expressions (condition) in default parameter value of inner component
      arg("""
          component InvalidComp34 {
            component Inner(int p = i ? -2 : 2) {
              port in boolean i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in conditional expressions (then) in default parameter value of inner component
      arg("""
          component InvalidComp35 {
            component Inner(int p = true ? i : 2) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in conditional expressions (else) in default parameter value of inner component
      arg("""
          component InvalidComp36 {
            component Inner(int p = true ? -2 : i) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in bracket expressions in default parameter value of inner component
      arg("""
          component InvalidComp37 {
            component Inner(int p = (i)) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in shift expressions in default parameter value of inner component
      arg("""
          component InvalidComp38 {
            component Inner(int p1 = i << 1, int p2 = 1 << i,
                            int p3 = i >> 1, int p4 = 1 >> i,
                            int p5 = i >>> 1, int p6 = 1 >>> i) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in binary expressions in default parameter value of inner component
      arg("""
          component InvalidComp39 {
            component Inner(boolean p1 = i & true, boolean p2 = true & i,
                            boolean p3 = i ^ true, boolean p4 = true ^ i,
                            boolean p5 = i | true, boolean p6 = true | i) {
              port in boolean i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in method call argument in default parameter value of inner component
      arg("""
          import montiarc.test.FunctionWithIntParameter;
          component InvalidComp40 {
            component Inner(int v = FunctionWithIntParameter(i)) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
