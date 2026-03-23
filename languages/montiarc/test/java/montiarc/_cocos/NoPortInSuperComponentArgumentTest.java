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

import java.nio.file.Paths;
import java.util.stream.Stream;

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoPortInSuperComponentArgument}.
 */
class NoPortInSuperComponentArgumentTest extends MontiArcTestBase {

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
      // subcomponent instantiation
      arg("""
        import montiarc.test.ComponentType;
        component ValidComp4 extends ComponentType {
          port in int i;
          port out int o;
        }
        """
      ),
      // subcomponent instantiation with literal argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidComp4 extends ComponentTypeWithIntParameter(1) {
          port in int i;
          port out int o;
        }
        """
      ),
      // subcomponent instantiation with parameter argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidComp5(int p) extends ComponentTypeWithIntParameter(p) {
          port in int i;
          port out int o;
        }
        """
      ),
      // subcomponent instantiation with oo-type with field parameter argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        import montiarc.test.OOTypeWithFieldIO;
        component ValidComp6(OOTypeWithFieldIO p) extends ComponentTypeWithIntParameter(p.i) {
          port in int i;
        }
        """
      ),
      // subcomponent instantiation with oo-type with field parameter argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        import montiarc.test.OOTypeWithFieldIO;
        component ValidComp7(OOTypeWithFieldIO p) extends ComponentTypeWithIntParameter(p.o) {
          port out int o;
        }
        """
      ),
      // subcomponent instantiation with oo-type with function parameter argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        import montiarc.test.OOTypeWithFunctionIO;
        component ValidComp9(OOTypeWithFunctionIO p) extends ComponentTypeWithIntParameter(p.i()) {
          port in int i;
        }
        """
      ),
      // subcomponent instantiation with oo-type with function parameter argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        import montiarc.test.OOTypeWithFunctionIO;
        component ValidComp19(OOTypeWithFunctionIO p) extends ComponentTypeWithIntParameter(p.o()) {
          port out int o;
        }
        """
      ),
      // subcomponent instantiation with static field argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp11 extends ComponentTypeWithIntParameter(OOTypeWithStaticFieldIO.i) {
          port in int i;
        }
        """
      ),
      // subcomponent instantiation with static field argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp11 extends ComponentTypeWithIntParameter(OOTypeWithStaticFieldIO.o) {
          port out int o;
        }
        """
      ),
      // subcomponent instantiation with static function argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp13 extends ComponentTypeWithIntParameter(OOTypeWithStaticFunctionIO.i()) {
          port in int i;
        }
        """
      ),
      // subcomponent instantiation with static function argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp13 extends ComponentTypeWithIntParameter(OOTypeWithStaticFunctionIO.o()) {
          port out int o;
        }
        """
      ),
      // subcomponent instantiation with named parameter and literal argument
      arg("""
        import montiarc.test.ComponentTypeWithIntIOParameters;
        component ValidComp15 extends ComponentTypeWithIntIOParameters(i = 1, o = 1) {
          port in int i;
          port out int o;
        }
        """
      ),
      // subcomponent instantiation with named parameter and parameter argument
      arg("""
        import montiarc.test.ComponentTypeWithIntIOParameters;
        component ValidComp16(int p) extends ComponentTypeWithIntIOParameters(i = p, o = p) {
          port in int i;
          port out int o;
        }
        """
      ),
      // subcomponent instantiation with named parameter and oo-type with field parameter argument
      arg("""
        import montiarc.test.ComponentTypeWithIntIOParameters;
        import montiarc.test.OOTypeWithFieldIO;
        component ValidComp17(OOTypeWithFieldIO p) extends ComponentTypeWithIntIOParameters(i = p.i, o = p.o) {
          port in int i;
          port out int o;
        }
        """
      ),
      // subcomponent instantiation with named parameter and oo-type with function parameter argument
      arg("""
        import montiarc.test.ComponentTypeWithIntIOParameters;
        import montiarc.test.OOTypeWithFunctionIO;
        component ValidComp18(OOTypeWithFunctionIO p) extends ComponentTypeWithIntIOParameters(i = p.i(), o = p.o()) {
          port in int i;
          port out int o;
        }
        """
      ),
      // subcomponent instantiation with named parameter and static field argument
      arg("""
        import montiarc.test.ComponentTypeWithIntIOParameters;
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp19 extends ComponentTypeWithIntIOParameters(i = OOTypeWithStaticFieldIO.i,
                                                                  o = OOTypeWithStaticFieldIO.o) {
          port in int i;
          port out int o;
        }
        """
      ),
      // subcomponent instantiation with named parameter and static function argument
      arg("""
        import montiarc.test.ComponentTypeWithIntIOParameters;
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp20 extends ComponentTypeWithIntIOParameters(i = OOTypeWithStaticFunctionIO.i(),
                                                                  o = OOTypeWithStaticFunctionIO.o()) {
          port in int i;
          port out int o;
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      //  input port in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp1 extends ComponentTypeWithIntParameter(i) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  output port in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp2 extends ComponentTypeWithIntParameter(o) {
            port out int o;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in assignment in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp3 extends ComponentTypeWithIntParameter((o = 1)) {
            port out int o;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in inc prefix expression in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp4 extends ComponentTypeWithIntParameter(++i) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in dec prefix expression in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp5 extends ComponentTypeWithIntParameter(--i) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in inc suffix expression in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp6 extends ComponentTypeWithIntParameter(i++) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in dec suffix expression in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp7 extends ComponentTypeWithIntParameter(i--) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in boolean not expression in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp8 extends ComponentTypeWithBooleanParameter(~i) {
            port in boolean i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in logical not expression in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp9 extends ComponentTypeWithBooleanParameter(!i) {
            port in boolean i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in multiply expressions (left) in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp10 extends ComponentTypeWithIntParameter(i * 2) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in multiply expressions (right) in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp11 extends ComponentTypeWithIntParameter(2 * i) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in multiply expressions (both) in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp12 extends ComponentTypeWithIntParameter(i * i) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in infix expressions in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp13 extends ComponentTypeWithIntParameter(2 / i2),
                                   ComponentTypeWithIntParameter(i2 / 2),
                                   ComponentTypeWithIntParameter(2 % i2),
                                   ComponentTypeWithIntParameter(i2 % 2),
                                   ComponentTypeWithIntParameter(2 + i2),
                                   ComponentTypeWithIntParameter(i2 + 2),
                                   ComponentTypeWithIntParameter(2 - i2),
                                   ComponentTypeWithIntParameter(i2 - 2),
                                   ComponentTypeWithBooleanParameter(i2 <= 2),
                                   ComponentTypeWithBooleanParameter(2 <= i2),
                                   ComponentTypeWithBooleanParameter(i2 >= 2),
                                   ComponentTypeWithBooleanParameter(2 >= i2),
                                   ComponentTypeWithBooleanParameter(i2 < 2),
                                   ComponentTypeWithBooleanParameter(2 < i2),
                                   ComponentTypeWithBooleanParameter(i2 > 2),
                                   ComponentTypeWithBooleanParameter(2 > i2),
                                   ComponentTypeWithBooleanParameter(i2 == 2),
                                   ComponentTypeWithBooleanParameter(2 == i2),
                                   ComponentTypeWithBooleanParameter(i2 != 2),
                                   ComponentTypeWithBooleanParameter(2 != i2),
                                   ComponentTypeWithBooleanParameter(i4 && 2),
                                   ComponentTypeWithBooleanParameter(2 && i4),
                                   ComponentTypeWithBooleanParameter(i4 || 2),
                                   ComponentTypeWithBooleanParameter(2 || i4) {
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
      //  input port in conditional expressions (condition) in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp14 extends ComponentTypeWithIntParameter(i ? -2 : 2) {
            port in boolean i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in conditional expressions (then) in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp15 extends ComponentTypeWithIntParameter(true ? i : 2) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in conditional expressions (else) in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp16 extends ComponentTypeWithIntParameter(true ? -2 : i) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in bracket expressions in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp17 extends ComponentTypeWithIntParameter((i)) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in shift expressions in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp18 extends ComponentTypeWithIntParameter(i << 1),
                                   ComponentTypeWithIntParameter(1 << i),
                                   ComponentTypeWithIntParameter(i >> 1),
                                   ComponentTypeWithIntParameter(1 >> i),
                                   ComponentTypeWithIntParameter(i >>> 1),
                                   ComponentTypeWithIntParameter(1 >>> i) {
            port in int i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in binary expressions in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp19 extends ComponentTypeWithBooleanParameter(i & true),
                                   ComponentTypeWithBooleanParameter(true & i),
                                   ComponentTypeWithBooleanParameter(i ^ true),
                                   ComponentTypeWithBooleanParameter(true ^ i),
                                   ComponentTypeWithBooleanParameter(i | true),
                                   ComponentTypeWithBooleanParameter(true | i) {
            port in boolean i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in method call argument in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          import montiarc.test.FunctionWithIntParameter;
          component InvalidComp20 extends ComponentTypeWithIntParameter(FunctionWithIntParameter(i)) {
            port in boolean i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp21 {
            component Inner extends ComponentTypeWithIntParameter(p = i) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  output port in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp22 {
            component Inner extends ComponentTypeWithIntParameter(p = o) {
              port out int o;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in assignment in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp23 {
            component Inner extends ComponentTypeWithIntParameter(p = i = 1) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in inc prefix expression in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp24 {
            component Inner extends ComponentTypeWithIntParameter(p = ++i) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in dec prefix expression in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp25 {
            component Inner extends ComponentTypeWithIntParameter(p = --i) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in inc suffix expression in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp26 {
            component Inner extends ComponentTypeWithIntParameter(p = i++) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in dec suffix expression in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp27 {
            component Inner extends ComponentTypeWithIntParameter(p = i--) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in boolean not expression in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp28 {
            component Inner extends ComponentTypeWithBooleanParameter(p = ~i) {
              port in boolean i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in logical not expression in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp29 {
            component Inner extends ComponentTypeWithBooleanParameter(p = !i) {
              port in boolean i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in multiply expressions (left) in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp30 {
            component Inner extends ComponentTypeWithIntParameter(p = i * 2) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in multiply expressions (right) in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp31 {
            component Inner extends ComponentTypeWithIntParameter(p = 2 * i) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  port in multiply expressions (both) in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp32 {
            component Inner extends ComponentTypeWithIntParameter(p = i * i) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in infix expressions in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp33 {
            component Inner extends ComponentTypeWithIntParameter(p = 2 / i2),
                                    ComponentTypeWithIntParameter(p = i2 / 2),
                                    ComponentTypeWithIntParameter(p = 2 % i2),
                                    ComponentTypeWithIntParameter(p = i2 % 2),
                                    ComponentTypeWithIntParameter(p = 2 + i2),
                                    ComponentTypeWithIntParameter(p = i2 + 2),
                                    ComponentTypeWithIntParameter(p = 2 - i2),
                                    ComponentTypeWithIntParameter(p = i2 - 2),
                                    ComponentTypeWithBooleanParameter(p = i2 <= 2),
                                    ComponentTypeWithBooleanParameter(p = 2 <= i2),
                                    ComponentTypeWithBooleanParameter(p = i2 >= 2),
                                    ComponentTypeWithBooleanParameter(p = 2 >= i2),
                                    ComponentTypeWithBooleanParameter(p = i2 < 2),
                                    ComponentTypeWithBooleanParameter(p = 2 < i2),
                                    ComponentTypeWithBooleanParameter(p = i2 > 2),
                                    ComponentTypeWithBooleanParameter(p = 2 > i2),
                                    ComponentTypeWithBooleanParameter(p = i2 == 2),
                                    ComponentTypeWithBooleanParameter(p = 2 == i2),
                                    ComponentTypeWithBooleanParameter(p = i2 != 2),
                                    ComponentTypeWithBooleanParameter(p = 2 != i2),
                                    ComponentTypeWithBooleanParameter(p = i4 && 2),
                                    ComponentTypeWithBooleanParameter(p = 2 && i4),
                                    ComponentTypeWithBooleanParameter(p = i4 || 2),
                                    ComponentTypeWithBooleanParameter(p = 2 || i4) {
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
      //  input port in conditional expressions (condition) in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp34 {
            component Inner extends ComponentTypeWithIntParameter(p = i ? -2 : 2) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in conditional expressions (then) in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp35 {
            component Inner extends ComponentTypeWithIntParameter(p = true ? i : 2) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in conditional expressions (else) in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp36 {
            component Inner extends ComponentTypeWithIntParameter(p = true ? -2 : i) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in bracket expressions in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp37 {
            component Inner extends ComponentTypeWithIntParameter(p = (i)) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in shift expressions in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp38 {
            component Inner extends ComponentTypeWithIntParameter(p = i << 1),
                                    ComponentTypeWithIntParameter(p = 1 << i),
                                    ComponentTypeWithIntParameter(p = i >> 1),
                                    ComponentTypeWithIntParameter(p = 1 >> i),
                                    ComponentTypeWithIntParameter(p = i >>> 1),
                                    ComponentTypeWithIntParameter(p = 1 >>> i) {
              port in int i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in binary expressions in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp39 {
            component Inner extends ComponentTypeWithBooleanParameter(p = i & true),
                                    ComponentTypeWithBooleanParameter(p = true & i),
                                    ComponentTypeWithBooleanParameter(p = i ^ true),
                                    ComponentTypeWithBooleanParameter(p = true ^ i),
                                    ComponentTypeWithBooleanParameter(p = i | true),
                                    ComponentTypeWithBooleanParameter(p = true | i) {
              port in boolean i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      //  input port in method call argument in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          import montiarc.test.FunctionWithIntParameter;
          component InvalidComp40 {
            component Inner extends ComponentTypeWithIntParameter(p = FunctionWithIntParameter(i)) {
              port in boolean i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
