/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.NoPortInSubcomponentArgument;
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
 * The class under test ist {@link NoPortInSubcomponentArgument}.
 */
class NoPortInSubcomponentArgumentTest extends MontiArcTestBase {

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
    checker.addCoCo(new NoPortInSubcomponentArgument());

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
    checker.addCoCo(new NoPortInSubcomponentArgument());

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
      // subcomponent instantiation without arguments
      arg("""
        import montiarc.test.ComponentType;
        component ValidComp4 {
          port in int i;
          port out int o;
          ComponentType sub;
        }
        """
      ),
      // subcomponent instantiation with literal argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidComp4 {
          port in int i;
          port out int o;
          ComponentTypeWithIntParameter sub1(1);
          ComponentTypeWithIntParameter sub2(1), sub3(-1);
        }
        """
      ),
      // subcomponent instantiation with parameter argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidComp5(int p) {
          port in int i;
          port out int o;
          ComponentTypeWithIntParameter sub1(p);
          ComponentTypeWithIntParameter sub2(p), sub3(p);
        }
        """
      ),
      // subcomponent instantiation with oo-type with field parameter argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        import montiarc.test.OOTypeWithFieldIO;
        component ValidComp6(OOTypeWithFieldIO p) {
          port in int i;
          port out int o;
          ComponentTypeWithIntParameter sub1(p.i);
          ComponentTypeWithIntParameter sub2(p.o);
          ComponentTypeWithIntParameter sub3(p.i), sub4(p.i);
          ComponentTypeWithIntParameter sub3(p.o), sub4(p.o);
        }
        """
      ),
      // subcomponent instantiation with oo-type with function parameter argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        import montiarc.test.OOTypeWithFunctionIO;
        component ValidComp7(OOTypeWithFunctionIO p) {
          port in int i;
          port out int o;
          ComponentTypeWithIntParameter sub1(p.i());
          ComponentTypeWithIntParameter sub2(p.o());
          ComponentTypeWithIntParameter sub3(p.i()), sub4(p.i());
          ComponentTypeWithIntParameter sub5(p.o()), sub6(p.o());
        }
        """
      ),
      // subcomponent instantiation with static field argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp8 {
          port in int i;
          port out int o;
          ComponentTypeWithIntParameter sub1(OOTypeWithStaticFieldIO.i);
          ComponentTypeWithIntParameter sub2(OOTypeWithStaticFieldIO.o);
          ComponentTypeWithIntParameter sub3(OOTypeWithStaticFieldIO.i), sub4(OOTypeWithStaticFieldIO.i);
          ComponentTypeWithIntParameter sub5(OOTypeWithStaticFieldIO.o), sub6(OOTypeWithStaticFieldIO.o);
        }
        """
      ),
      // subcomponent instantiation with static function argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp9 {
          port in int i;
          port out int o;
          ComponentTypeWithIntParameter sub1(OOTypeWithStaticFunctionIO.i());
          ComponentTypeWithIntParameter sub2(OOTypeWithStaticFunctionIO.o());
          ComponentTypeWithIntParameter sub3(OOTypeWithStaticFunctionIO.i()), sub4(OOTypeWithStaticFunctionIO.i());
          ComponentTypeWithIntParameter sub5(OOTypeWithStaticFunctionIO.o()), sub6(OOTypeWithStaticFunctionIO.o());
        }
        """
      ),
      // subcomponent instantiation with named parameter and literal argument
      arg("""
        import montiarc.test.ComponentTypeWithIntIOParameters;
        component ValidComp10 {
          port in int i;
          port out int o;
          ComponentTypeWithIntIOParameters sub1(i = 1);
          ComponentTypeWithIntIOParameters sub2(o = 1);
          ComponentTypeWithIntIOParameters sub3(i = 1), sub4(i = -1);
          ComponentTypeWithIntIOParameters sub5(o = 1), sub6(o = -1);
        }
        """
      ),
      // subcomponent instantiation with named parameter and parameter argument
      arg("""
        import montiarc.test.ComponentTypeWithIntIOParameters;
        component ValidComp11(int p) {
          port in int i;
          port out int o;
          ComponentTypeWithIntIOParameters sub1(i = p);
          ComponentTypeWithIntIOParameters sub2(o = p);
          ComponentTypeWithIntIOParameters sub3(i = p), sub4(i = p);
          ComponentTypeWithIntIOParameters sub5(o = p), sub6(o = p);
        }
        """
      ),
      // subcomponent instantiation with named parameter and oo-type with field parameter argument
      arg("""
        import montiarc.test.ComponentTypeWithIntIOParameters;
        import montiarc.test.OOTypeWithFieldIO;
        component ValidComp12(OOTypeWithFieldIO p) {
          port in int i;
          port out int o;
          ComponentTypeWithIntIOParameters sub1(i = p.i);
          ComponentTypeWithIntIOParameters sub2(o = p.o);
          ComponentTypeWithIntIOParameters sub3(i = p.o);
          ComponentTypeWithIntIOParameters sub4(o = p.i);
          ComponentTypeWithIntIOParameters sub5(i = p.i), sub6(i = p.i);
          ComponentTypeWithIntIOParameters sub7(o = p.o), sub8(o = p.o);
        }
        """
      ),
      // subcomponent instantiation with named parameter and oo-type with function parameter argument
      arg("""
        import montiarc.test.ComponentTypeWithIntIOParameters;
        import montiarc.test.OOTypeWithFunctionIO;
        component ValidComp13(OOTypeWithFunctionIO p) {
          port in int i;
          port out int o;
          ComponentTypeWithIntIOParameters sub1(i = p.i());
          ComponentTypeWithIntIOParameters sub2(o = p.o());
          ComponentTypeWithIntIOParameters sub3(i = p.o());
          ComponentTypeWithIntIOParameters sub4(o = p.i());
          ComponentTypeWithIntIOParameters sub5(i = p.i()), sub6(i = p.i());
          ComponentTypeWithIntIOParameters sub7(o = p.o()), sub8(o = p.o());
        }
        """
      ),
      // subcomponent instantiation with named parameter and static field argument
      arg("""
        import montiarc.test.ComponentTypeWithIntIOParameters;
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp14 {
          port in int i;
          port out int o;
          ComponentTypeWithIntIOParameters sub1(i = OOTypeWithStaticFieldIO.i);
          ComponentTypeWithIntIOParameters sub2(o = OOTypeWithStaticFieldIO.o);
          ComponentTypeWithIntIOParameters sub3(i = OOTypeWithStaticFieldIO.o);
          ComponentTypeWithIntIOParameters sub4(o = OOTypeWithStaticFieldIO.i);
          ComponentTypeWithIntIOParameters sub5(i = OOTypeWithStaticFieldIO.i), sub6(i = OOTypeWithStaticFieldIO.i);
          ComponentTypeWithIntIOParameters sub7(o = OOTypeWithStaticFieldIO.o), sub8(o = OOTypeWithStaticFieldIO.o);
        }
        """
      ),
      // subcomponent instantiation with named parameter and static function argument
      arg("""
        import montiarc.test.ComponentTypeWithIntIOParameters;
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp15 {
          port in int i;
          port out int o;
          ComponentTypeWithIntIOParameters sub1(i = OOTypeWithStaticFunctionIO.i());
          ComponentTypeWithIntIOParameters sub2(o = OOTypeWithStaticFunctionIO.o());
          ComponentTypeWithIntIOParameters sub3(i = OOTypeWithStaticFunctionIO.o());
          ComponentTypeWithIntIOParameters sub4(o = OOTypeWithStaticFunctionIO.i());
          ComponentTypeWithIntIOParameters sub5(i = OOTypeWithStaticFunctionIO.i()), sub6(i = OOTypeWithStaticFunctionIO.i());
          ComponentTypeWithIntIOParameters sub7(o = OOTypeWithStaticFunctionIO.o()), sub8(o = OOTypeWithStaticFunctionIO.o());
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // read value from input port in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp1 {
            port in int i;
            ComponentTypeWithIntParameter sub(i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from output port in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp2 {
            port out int o;
            ComponentTypeWithIntParameter sub(o);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in assignment in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp3 {
            port out int o;
            ComponentTypeWithIntParameter sub((o = 1));
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in inc prefix expression in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp4 {
            port in int i;
            ComponentTypeWithIntParameter sub(++i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in dec prefix expression in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp5 {
            port in int i;
            ComponentTypeWithIntParameter sub(--i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in inc suffix expression in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp6 {
            port in int i;
            ComponentTypeWithIntParameter sub(i++);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in dec suffix expression in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp7 {
            port in int i;
            ComponentTypeWithIntParameter sub(i--);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in boolean not expression in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp8 {
            port in boolean i;
            ComponentTypeWithBooleanParameter sub(~i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in logical not expression in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp9 {
            port in boolean i;
            ComponentTypeWithBooleanParameter sub(!i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in multiply expressions (left) in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp10 {
            port in int i;
            ComponentTypeWithIntParameter sub(i * 2);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in multiply expressions (right) in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp11 {
            port in int i;
            ComponentTypeWithIntParameter sub(2 * i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in multiply expressions (both) in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp12 {
            port in int i;
            ComponentTypeWithIntParameter sub(i * i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in infix expressions in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp13 {
            port in int i1, i2;
            port in boolean i3, i4;
            ComponentTypeWithIntParameter sub(2 / i2);
            ComponentTypeWithIntParameter sub2(i2 / 2);
            ComponentTypeWithIntParameter sub3(2 % i2);
            ComponentTypeWithIntParameter sub4(i2 % 2);
            ComponentTypeWithIntParameter sub5(2 + i2);
            ComponentTypeWithIntParameter sub6(i2 + 2);
            ComponentTypeWithIntParameter sub7(2 - i2);
            ComponentTypeWithIntParameter sub8(i2 - 2);
            ComponentTypeWithBooleanParameter sub9(i2 <= 2);
            ComponentTypeWithBooleanParameter sub10(2 <= i2);
            ComponentTypeWithBooleanParameter sub11(i2 >= 2);
            ComponentTypeWithBooleanParameter sub12(2 >= i2);
            ComponentTypeWithBooleanParameter sub13(i2 < 2);
            ComponentTypeWithBooleanParameter sub14(2 < i2);
            ComponentTypeWithBooleanParameter sub15(i2 > 2);
            ComponentTypeWithBooleanParameter sub16(2 > i2);
            ComponentTypeWithBooleanParameter sub17(i2 == 2);
            ComponentTypeWithBooleanParameter sub18(2 == i2);
            ComponentTypeWithBooleanParameter sub19(i2 != 2);
            ComponentTypeWithBooleanParameter sub20(2 != i2);
            ComponentTypeWithBooleanParameter sub21(i4 && 2);
            ComponentTypeWithBooleanParameter sub22(2 && i4);
            ComponentTypeWithBooleanParameter sub23(i4 || 2);
            ComponentTypeWithBooleanParameter sub24(2 || i4);
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
      // read value from input port in conditional expressions (condition) in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp14 {
            port in boolean i;
            ComponentTypeWithIntParameter sub(i ? -2 : 2);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in conditional expressions (then) in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp15 {
            port in int i;
            ComponentTypeWithIntParameter sub(true ? i : 2);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in conditional expressions (else) in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp16 {
            port in int i;
            ComponentTypeWithIntParameter sub(true ? -2 : i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in bracket expressions in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp17 {
            port in int i;
            ComponentTypeWithIntParameter sub((i));
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in shift expressions in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp18 {
            port in int i;
            ComponentTypeWithIntParameter sub(i << 1);
            ComponentTypeWithIntParameter sub2(1 << i);
            ComponentTypeWithIntParameter sub3(i >> 1);
            ComponentTypeWithIntParameter sub4(1 >> i);
            ComponentTypeWithIntParameter sub5(i >>> 1);
            ComponentTypeWithIntParameter sub6(1 >>> i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in binary expressions in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp19 {
            port in boolean i;
            ComponentTypeWithBooleanParameter sub1(i & true);
            ComponentTypeWithBooleanParameter sub2(true & i);
            ComponentTypeWithBooleanParameter sub3(i ^ true);
            ComponentTypeWithBooleanParameter sub4(true ^ i);
            ComponentTypeWithBooleanParameter sub5(i | true);
            ComponentTypeWithBooleanParameter sub6(true | i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in method call argument in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          import montiarc.test.FunctionWithIntParameter;
          component InvalidComp20 {
            port in boolean i;
            ComponentTypeWithIntParameter sub(FunctionWithIntParameter(i));
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp21 {
            port in int i;
            ComponentTypeWithIntParameter sub(p = i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from output port in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp22 {
            port out int o;
            ComponentTypeWithIntParameter sub(p = o);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in assignment in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp23 {
            port in int i;
            ComponentTypeWithIntParameter sub(p = i = 1);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in inc prefix expression in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp24 {
            port in int i;
            ComponentTypeWithIntParameter sub(p = ++i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in dec prefix expression in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp25 {
            port in int i;
            ComponentTypeWithIntParameter sub(p = --i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in inc suffix expression in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp26 {
            port in int i;
            ComponentTypeWithIntParameter sub(p = i++);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in dec suffix expression in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp27 {
            port in int i;
            ComponentTypeWithIntParameter sub(p = i--);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in boolean not expression in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp28 {
            port in boolean i;
            ComponentTypeWithBooleanParameter sub(p = ~i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in logical not expression in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp29 {
            port in boolean i;
            ComponentTypeWithBooleanParameter sub(p = !i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in multiply expressions (left) in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp30 {
            port in int i;
            ComponentTypeWithIntParameter sub(p = i * 2);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in multiply expressions (right) in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp31 {
            port in int i;
            ComponentTypeWithIntParameter sub(p = 2 * i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from port in multiply expressions (both) in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp32 {
            port in int i;
            ComponentTypeWithIntParameter sub(p = i * i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in infix expressions in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp33 {
            port in int i1, i2;
            port in boolean i3, i4;
            ComponentTypeWithIntParameter sub(p = 2 / i2);
            ComponentTypeWithIntParameter sub2(p = i2 / 2);
            ComponentTypeWithIntParameter sub3(p = 2 % i2);
            ComponentTypeWithIntParameter sub4(p = i2 % 2);
            ComponentTypeWithIntParameter sub5(p = 2 + i2);
            ComponentTypeWithIntParameter sub6(p = i2 + 2);
            ComponentTypeWithIntParameter sub7(p = 2 - i2);
            ComponentTypeWithIntParameter sub8(p = i2 - 2);
            ComponentTypeWithBooleanParameter sub9(p = i2 <= 2);
            ComponentTypeWithBooleanParameter sub10(p = 2 <= i2);
            ComponentTypeWithBooleanParameter sub11(p = i2 >= 2);
            ComponentTypeWithBooleanParameter sub12(p = 2 >= i2);
            ComponentTypeWithBooleanParameter sub13(p = i2 < 2);
            ComponentTypeWithBooleanParameter sub14(p = 2 < i2);
            ComponentTypeWithBooleanParameter sub15(p = i2 > 2);
            ComponentTypeWithBooleanParameter sub16(p = 2 > i2);
            ComponentTypeWithBooleanParameter sub17(p = i2 == 2);
            ComponentTypeWithBooleanParameter sub18(p = 2 == i2);
            ComponentTypeWithBooleanParameter sub19(p = i2 != 2);
            ComponentTypeWithBooleanParameter sub20(p = 2 != i2);
            ComponentTypeWithBooleanParameter sub21(p = i4 && 2);
            ComponentTypeWithBooleanParameter sub22(p = 2 && i4);
            ComponentTypeWithBooleanParameter sub23(p = i4 || 2);
            ComponentTypeWithBooleanParameter sub24(p = 2 || i4);
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
      // read value from input port in conditional expressions (condition) in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp34 {
            port in boolean i;
            ComponentTypeWithIntParameter sub(i ? -2 : 2);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in conditional expressions (then) in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp35 {
            port in int i;
            ComponentTypeWithIntParameter sub(true ? i : 2);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in conditional expressions (else) in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp36 {
            port in int i;
            ComponentTypeWithIntParameter sub(true ? -2 : i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in bracket expressions in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp37 {
            port in int i;
            ComponentTypeWithIntParameter sub((i));
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in shift expressions in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp38 {
            port in int i;
            ComponentTypeWithIntParameter sub(i << 1);
            ComponentTypeWithIntParameter sub2(1 << i);
            ComponentTypeWithIntParameter sub3(i >> 1);
            ComponentTypeWithIntParameter sub4(1 >> i);
            ComponentTypeWithIntParameter sub5(i >>> 1);
            ComponentTypeWithIntParameter sub6(1 >>> i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in binary expressions in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp39 {
            port in boolean i;
            ComponentTypeWithBooleanParameter sub1(p = i & true);
            ComponentTypeWithBooleanParameter sub2(p = true & i);
            ComponentTypeWithBooleanParameter sub3(p = i ^ true);
            ComponentTypeWithBooleanParameter sub4(p = true ^ i);
            ComponentTypeWithBooleanParameter sub5(p = i | true);
            ComponentTypeWithBooleanParameter sub6(p = true | i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // read value from input port in method call argument in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          import montiarc.test.FunctionWithIntParameter;
          component InvalidComp40 {
            port in boolean i;
            ComponentTypeWithIntParameter sub(p = FunctionWithIntParameter(i));
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
