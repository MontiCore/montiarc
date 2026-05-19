/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.NoFieldInSuperComponentArgument;
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

import static montiarc.util.ArcError.FIELD_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

public class NoFieldInSuperComponentArgumentTest extends MontiArcTestBase {

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
    checker.addCoCo(new NoFieldInSuperComponentArgument());

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
    checker.addCoCo(new NoFieldInSuperComponentArgument());

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
          int x = 1;
          int z = x + y;
          int y = x + 1 + w;
          int w = 3 + x;
        }
        """
      ),
      // subcomponent instantiation
      arg("""
        import montiarc.test.ComponentType;
        component ValidComp3 extends ComponentType {
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
      // supercomponent instantiation with field argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidComp1 extends ComponentTypeWithIntParameter(x) {
          int x = 1;
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // supercomponent instantiation with field argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidComp2 extends ComponentTypeWithIntParameter((p = 1)) {
          int p = 1;
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // supercomponent instantiation with field argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidComp3 extends ComponentTypeWithIntParameter(++p) {
          int p = 1;
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // supercomponent instantiation with field argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidComp4 extends ComponentTypeWithIntParameter(--p) {
          int p = 1;
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // supercomponent instantiation with field argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidComp5 extends ComponentTypeWithIntParameter(p--) {
          int p = 1;
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // supercomponent instantiation with field argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidComp6 extends ComponentTypeWithIntParameter(~p) {
          boolean p = true;
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // supercomponent instantiation with field argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidComp7 extends ComponentTypeWithIntParameter(2 * p) {
          int p = 1;
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // supercomponent instantiation with field argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidComp8 extends ComponentTypeWithIntParameter(p * p) {
          int p = 1;
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in infix expressions in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp9 extends ComponentTypeWithIntParameter(2 / i2),
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
                                   ComponentTypeWithBooleanParameter(b && 2),
                                   ComponentTypeWithBooleanParameter(2 && b),
                                   ComponentTypeWithBooleanParameter(b || 2),
                                   ComponentTypeWithBooleanParameter(2 || b) {
            int i1 = 1;
            int i2 = 1;
            boolean b = true;
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in conditional expressions (condition) in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp10 extends ComponentTypeWithIntParameter(b ? -2 : 2) {
            boolean b = 1;
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in conditional expressions (then) in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp11 extends ComponentTypeWithIntParameter(true ? i : 2) {
            int i = 1;
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in conditional expressions (else) in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp12 extends ComponentTypeWithIntParameter(true ? -2 : i) {
            int i = 1;
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in bracket expressions in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp13 extends ComponentTypeWithIntParameter((i)) {
            int i = 1;
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in shift expressions in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp14 extends ComponentTypeWithIntParameter(i << 1),
                                   ComponentTypeWithIntParameter(1 << i),
                                   ComponentTypeWithIntParameter(i >> 1),
                                   ComponentTypeWithIntParameter(1 >> i),
                                   ComponentTypeWithIntParameter(i >>> 1),
                                   ComponentTypeWithIntParameter(1 >>> i) {
            int i = 1;
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in binary expressions in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp15 extends ComponentTypeWithBooleanParameter(b & true),
                                   ComponentTypeWithBooleanParameter(true & b),
                                   ComponentTypeWithBooleanParameter(b ^ true),
                                   ComponentTypeWithBooleanParameter(true ^ b),
                                   ComponentTypeWithBooleanParameter(b | true),
                                   ComponentTypeWithBooleanParameter(true | b) {
            boolean b = true;
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in method call argument in super component argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          import montiarc.test.FunctionWithIntParameter;
          component InvalidComp16 extends ComponentTypeWithIntParameter(FunctionWithIntParameter(b)) {
            boolean b = true;
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp17 {
            component Inner extends ComponentTypeWithIntParameter(p = i) {
              int i = 1;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field assignment in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp18 {
            component Inner extends ComponentTypeWithIntParameter(p = i = 1) {
              int i = 1;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field inc prefix expression in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp19 {
            component Inner extends ComponentTypeWithIntParameter(p = ++i) {
              int i = 1;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field dec prefix expression in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp20 {
            component Inner extends ComponentTypeWithIntParameter(p = --i) {
              int i = 1;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field inc suffix expression in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp21 {
            component Inner extends ComponentTypeWithIntParameter(p = i++) {
              int i = 1;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field dec suffix expression in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp22 {
            component Inner extends ComponentTypeWithIntParameter(p = i--) {
              int i = 1;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field boolean not expression in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp23 {
            component Inner extends ComponentTypeWithBooleanParameter(p = ~b) {
              boolean b = true;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field logical not expression in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp24 {
            component Inner extends ComponentTypeWithBooleanParameter(p = !b) {
             boolean b = true;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field multiply expressions (left) in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp25 {
            component Inner extends ComponentTypeWithIntParameter(p = i * 2) {
              int i = 1;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field multiply expressions (right) in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp26 {
            component Inner extends ComponentTypeWithIntParameter(p = 2 * i) {
              int i = 1;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field multiply expressions (both) in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp27 {
            component Inner extends ComponentTypeWithIntParameter(p = i * i) {
              int i = 1;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in infix expressions in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp28 {
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
                                    ComponentTypeWithBooleanParameter(p = b && 2),
                                    ComponentTypeWithBooleanParameter(p = 2 && b),
                                    ComponentTypeWithBooleanParameter(p = b || 2),
                                    ComponentTypeWithBooleanParameter(p = 2 || b) {
              int i1 = 1;
              int i2 = 1;
              boolean b = true;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in conditional expressions (condition) in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp29 {
            component Inner extends ComponentTypeWithIntParameter(p = i ? -2 : 2) {
              int i = 1;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in conditional expressions (then) in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp30 {
            component Inner extends ComponentTypeWithIntParameter(p = true ? i : 2) {
              int i = 1;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in conditional expressions (else) in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp31 {
            component Inner extends ComponentTypeWithIntParameter(p = true ? -2 : i) {
              int i = 1;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in bracket expressions in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp32 {
            component Inner extends ComponentTypeWithIntParameter(p = (i)) {
              int i = 1;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in shift expressions in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidComp33 {
            component Inner extends ComponentTypeWithIntParameter(p = i << 1),
                                    ComponentTypeWithIntParameter(p = 1 << i),
                                    ComponentTypeWithIntParameter(p = i >> 1),
                                    ComponentTypeWithIntParameter(p = 1 >> i),
                                    ComponentTypeWithIntParameter(p = i >>> 1),
                                    ComponentTypeWithIntParameter(p = 1 >>> i) {
              int i = 1;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in binary expressions in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp34 {
            component Inner extends ComponentTypeWithBooleanParameter(p = b & true),
                                    ComponentTypeWithBooleanParameter(p = true & b),
                                    ComponentTypeWithBooleanParameter(p = b ^ true),
                                    ComponentTypeWithBooleanParameter(p = true ^ b),
                                    ComponentTypeWithBooleanParameter(p = b | true),
                                    ComponentTypeWithBooleanParameter(p = true | b) {
              boolean b = true;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT, FIELD_REF_IN_STATIC_CONTEXT
      ),
      //  field in method call argument in super component argument of inner component
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          import montiarc.test.FunctionWithIntParameter;
          component InvalidComp35 {
            component Inner extends ComponentTypeWithIntParameter(p = FunctionWithIntParameter(b)) {
              boolean b = true;
            }
          }
          """,
        FIELD_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
