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
class NoPortInRefinementArgumentTest extends MontiArcTestBase {

  private static final String SYMBOLS_DIR = "symbols";

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
      // no ports, no refinement
      arg("component ValidComp1 { }"),
      // ports, no refinement
      arg("component ValidComp2 { port in int i; port out int o; }"),
      // refinement without arguments
      arg("import montiarc.test.ComponentType; component ValidComp3 refines ComponentType { port in int i; port out int o; }"),
      // refinement with a literal argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component ValidComp4 refines ComponentTypeWithIntParameter(1) { port in int i; port out int o; }"),
      // refinement argument from a parameter
      arg("import montiarc.test.ComponentTypeWithIntParameter; component ValidComp5(int p) refines ComponentTypeWithIntParameter(p) { port in int i; port out int o; }"),
      // refinement argument from a parameter's field (input port)
      arg("import montiarc.test.ComponentTypeWithIntParameter; import montiarc.test.OOTypeWithFieldIO; component ValidComp6(OOTypeWithFieldIO p) refines ComponentTypeWithIntParameter(p.i) { port in int i; }"),
      // refinement argument from a parameter's field (output port)
      arg("import montiarc.test.ComponentTypeWithIntParameter; import montiarc.test.OOTypeWithFieldIO; component ValidComp7(OOTypeWithFieldIO p) refines ComponentTypeWithIntParameter(p.o) { port out int o; }"),
      // refinement argument from a parameter's method (input port)
      arg("import montiarc.test.ComponentTypeWithIntParameter; import montiarc.test.OOTypeWithFunctionIO; component ValidComp8(OOTypeWithFunctionIO p) refines ComponentTypeWithIntParameter(p.i()) { port in int i; }"),
      // refinement argument from a parameter's method (output port)
      arg("import montiarc.test.ComponentTypeWithIntParameter; import montiarc.test.OOTypeWithFunctionIO; component ValidComp9(OOTypeWithFunctionIO p) refines ComponentTypeWithIntParameter(p.o()) { port out int o; }"),
      // refinement argument from an external type's static field (input port)
      arg("import montiarc.test.ComponentTypeWithIntParameter; import montiarc.test.OOTypeWithStaticFieldIO; component ValidComp10 refines ComponentTypeWithIntParameter(OOTypeWithStaticFieldIO.i) { port in int i; }"),
      // refinement argument from an external type's static field (output port)
      arg("import montiarc.test.ComponentTypeWithIntParameter; import montiarc.test.OOTypeWithStaticFieldIO; component ValidComp11 refines ComponentTypeWithIntParameter(OOTypeWithStaticFieldIO.o) { port out int o; }"),
      // refinement argument from an external type's static function (input port)
      arg("import montiarc.test.ComponentTypeWithIntParameter; import montiarc.test.OOTypeWithStaticFunctionIO; component ValidComp12 refines ComponentTypeWithIntParameter(OOTypeWithStaticFunctionIO.i()) { port in int i; }"),
      // refinement argument from an external type's static function (output port)
      arg("import montiarc.test.ComponentTypeWithIntParameter; import montiarc.test.OOTypeWithStaticFunctionIO; component ValidComp13 refines ComponentTypeWithIntParameter(OOTypeWithStaticFunctionIO.o()) { port out int o; }"),
      // refinement with keyed arguments as literals
      arg("import montiarc.test.ComponentTypeWithIntIOParameters; component ValidComp14 refines ComponentTypeWithIntIOParameters(i = 1, o = 1){ port in int i; port out int o; }"),
      // refinement with keyed arguments from a parameter
      arg("import montiarc.test.ComponentTypeWithIntIOParameters; component ValidComp15(int p) refines ComponentTypeWithIntIOParameters(i = p, o = p) { port in int i; port out int o; }"),
      // refinement with keyed arguments from a parameter's fields
      arg("import montiarc.test.ComponentTypeWithIntIOParameters; import montiarc.test.OOTypeWithFieldIO; component ValidComp16(OOTypeWithFieldIO p) refines ComponentTypeWithIntIOParameters(i = p.i, o = p.o) { port in int i; port out int o; }"),
      // refinement with keyed arguments from a parameter's methods
      arg("import montiarc.test.ComponentTypeWithIntIOParameters; import montiarc.test.OOTypeWithFunctionIO; component ValidComp17(OOTypeWithFunctionIO p) refines ComponentTypeWithIntIOParameters(i = p.i(), o = p.o()) { port in int i; port out int o; }"),
      // refinement with keyed arguments from an external type's static fields
      arg("import montiarc.test.ComponentTypeWithIntIOParameters; import montiarc.test.OOTypeWithStaticFieldIO; component ValidComp18 refines ComponentTypeWithIntIOParameters(i = OOTypeWithStaticFieldIO.i, o = OOTypeWithStaticFieldIO.o) { port in int i; port out int o; }"),
      // refinement with keyed arguments from an external type's static functions
      arg("import montiarc.test.ComponentTypeWithIntIOParameters; import montiarc.test.OOTypeWithStaticFunctionIO; component ValidComp19 refines ComponentTypeWithIntIOParameters(i = OOTypeWithStaticFunctionIO.i(), o = OOTypeWithStaticFunctionIO.o()) { port in int i; port out int o; }")
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // no input port in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp1 refines ComponentTypeWithIntParameter(i) { port in int i; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no output port in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp2 refines ComponentTypeWithIntParameter(o) { port out int o; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in assignment in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp3 refines ComponentTypeWithIntParameter((o = 1)) { port out int o; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in inc prefix expression in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp4 refines ComponentTypeWithIntParameter(++i) { port in int i; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in dec prefix expression in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp5 refines ComponentTypeWithIntParameter(--i) { port in int i; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in inc suffix expression in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp6 refines ComponentTypeWithIntParameter(i++) { port in int i; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in dec suffix expression in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp7 refines ComponentTypeWithIntParameter(i--) { port in int i; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in boolean not expression in refinement argument
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; component InvalidComp8 refines ComponentTypeWithBooleanParameter(~i) { port in boolean i; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in logical not expression in refinement argument
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; component InvalidComp9 refines ComponentTypeWithBooleanParameter(!i) { port in boolean i; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in multiply expressions (left) in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp10 refines ComponentTypeWithIntParameter(i * 2) { port in int i; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in multiply expressions (right) in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp11 refines ComponentTypeWithIntParameter(2 * i) { port in int i; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in multiply expressions (both) in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp12 refines ComponentTypeWithIntParameter(i * i) { port in int i; }",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in infix expressions in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; import montiarc.test.ComponentTypeWithBooleanParameter; component InvalidComp13 refines ComponentTypeWithIntParameter(2 / i2), ComponentTypeWithIntParameter(i2 / 2), ComponentTypeWithIntParameter(2 % i2), ComponentTypeWithIntParameter(i2 % 2), ComponentTypeWithIntParameter(2 + i2), ComponentTypeWithIntParameter(i2 + 2), ComponentTypeWithIntParameter(2 - i2), ComponentTypeWithIntParameter(i2 - 2), ComponentTypeWithBooleanParameter(i2 <= 2), ComponentTypeWithBooleanParameter(2 <= i2), ComponentTypeWithBooleanParameter(i2 >= 2), ComponentTypeWithBooleanParameter(2 >= i2), ComponentTypeWithBooleanParameter(i2 < 2), ComponentTypeWithBooleanParameter(2 < i2), ComponentTypeWithBooleanParameter(i2 > 2), ComponentTypeWithBooleanParameter(2 > i2), ComponentTypeWithBooleanParameter(i2 == 2), ComponentTypeWithBooleanParameter(2 == i2), ComponentTypeWithBooleanParameter(i2 != 2), ComponentTypeWithBooleanParameter(2 != i2), ComponentTypeWithBooleanParameter(i4 && 2), ComponentTypeWithBooleanParameter(2 && i4), ComponentTypeWithBooleanParameter(i4 || 2), ComponentTypeWithBooleanParameter(2 || i4) { port in int i1, i2; port in boolean i3, i4; }",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in conditional expressions (condition) in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp14 refines ComponentTypeWithIntParameter(i ? -2 : 2) { port in boolean i; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in conditional expressions (then) in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp15 refines ComponentTypeWithIntParameter(true ? i : 2) { port in int i; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in conditional expressions (else) in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp16 refines ComponentTypeWithIntParameter(true ? -2 : i) { port in int i; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in bracket expressions in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp17 refines ComponentTypeWithIntParameter((i)) { port in int i; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in shift expressions in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp18 refines ComponentTypeWithIntParameter(i << 1), ComponentTypeWithIntParameter(1 << i), ComponentTypeWithIntParameter(i >> 1), ComponentTypeWithIntParameter(1 >> i), ComponentTypeWithIntParameter(i >>> 1), ComponentTypeWithIntParameter(1 >>> i) { port in int i; }",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in binary expressions in refinement argument
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; component InvalidComp19 refines ComponentTypeWithBooleanParameter(i & true), ComponentTypeWithBooleanParameter(true & i), ComponentTypeWithBooleanParameter(i ^ true), ComponentTypeWithBooleanParameter(true ^ i), ComponentTypeWithBooleanParameter(i | true), ComponentTypeWithBooleanParameter(true | i) { port in boolean i; }",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in method call argument in refinement argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; import montiarc.test.FunctionWithIntParameter; component InvalidComp20 refines ComponentTypeWithIntParameter(FunctionWithIntParameter(i)) { port in boolean i; }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp21 { component Inner refines ComponentTypeWithIntParameter(p = i) { port in int i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no output port in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp22 { component Inner refines ComponentTypeWithIntParameter(p = o) { port out int o; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in assignment in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp23 { component Inner refines ComponentTypeWithIntParameter(p = i = 1) { port in int i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in inc prefix expression in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp24 { component Inner refines ComponentTypeWithIntParameter(p = ++i) { port in int i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in dec prefix expression in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp25 { component Inner refines ComponentTypeWithIntParameter(p = --i) { port in int i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in inc suffix expression in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp26 { component Inner refines ComponentTypeWithIntParameter(p = i++) { port in int i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in dec suffix expression in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp27 { component Inner refines ComponentTypeWithIntParameter(p = i--) { port in int i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in boolean not expression in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; component InvalidComp28 { component Inner refines ComponentTypeWithBooleanParameter(p = ~i) { port in boolean i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in logical not expression in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; component InvalidComp29 { component Inner refines ComponentTypeWithBooleanParameter(p = !i) { port in boolean i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in multiply expressions (left) in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp30 { component Inner refines ComponentTypeWithIntParameter(p = i * 2) { port in int i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in multiply expressions (right) in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp31 { component Inner refines ComponentTypeWithIntParameter(p = 2 * i) { port in int i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no port in multiply expressions (both) in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp32 { component Inner refines ComponentTypeWithIntParameter(p = i * i) { port in int i; } }",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in infix expressions in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; import montiarc.test.ComponentTypeWithBooleanParameter; component InvalidComp33 { component Inner refines ComponentTypeWithIntParameter(p = 2 / i2), ComponentTypeWithIntParameter(p = i2 / 2), ComponentTypeWithIntParameter(p = 2 % i2), ComponentTypeWithIntParameter(p = i2 % 2), ComponentTypeWithIntParameter(p = 2 + i2), ComponentTypeWithIntParameter(p = i2 + 2), ComponentTypeWithIntParameter(p = 2 - i2), ComponentTypeWithIntParameter(p = i2 - 2), ComponentTypeWithBooleanParameter(p = i2 <= 2), ComponentTypeWithBooleanParameter(p = 2 <= i2), ComponentTypeWithBooleanParameter(p = i2 >= 2), ComponentTypeWithBooleanParameter(p = 2 >= i2), ComponentTypeWithBooleanParameter(p = i2 < 2), ComponentTypeWithBooleanParameter(p = 2 < i2), ComponentTypeWithBooleanParameter(p = i2 > 2), ComponentTypeWithBooleanParameter(p = 2 > i2), ComponentTypeWithBooleanParameter(p = i2 == 2), ComponentTypeWithBooleanParameter(p = 2 == i2), ComponentTypeWithBooleanParameter(p = i2 != 2), ComponentTypeWithBooleanParameter(p = 2 != i2), ComponentTypeWithBooleanParameter(p = i4 && 2), ComponentTypeWithBooleanParameter(p = 2 && i4), ComponentTypeWithBooleanParameter(p = i4 || 2), ComponentTypeWithBooleanParameter(p = 2 || i4) { port in int i1, i2; port in boolean i3, i4; } }",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in conditional expressions (condition) in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp34 { component Inner refines ComponentTypeWithIntParameter(p = i ? -2 : 2) { port in int i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in conditional expressions (then) in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp35 { component Inner refines ComponentTypeWithIntParameter(p = true ? i : 2) { port in int i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in conditional expressions (else) in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp36 { component Inner refines ComponentTypeWithIntParameter(p = true ? -2 : i) { port in int i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in bracket expressions in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp37 { component Inner refines ComponentTypeWithIntParameter(p = (i)) { port in int i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in shift expressions in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; component InvalidComp38 { component Inner refines ComponentTypeWithIntParameter(p = i << 1), ComponentTypeWithIntParameter(p = 1 << i), ComponentTypeWithIntParameter(p = i >> 1), ComponentTypeWithIntParameter(p = 1 >> i), ComponentTypeWithIntParameter(p = i >>> 1), ComponentTypeWithIntParameter(p = 1 >>> i) { port in int i; }}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in binary expressions in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; component InvalidComp39 { component Inner refines ComponentTypeWithBooleanParameter(p = i & true), ComponentTypeWithBooleanParameter(p = true & i), ComponentTypeWithBooleanParameter(p = i ^ true), ComponentTypeWithBooleanParameter(p = true ^ i), ComponentTypeWithBooleanParameter(p = i | true), ComponentTypeWithBooleanParameter(p = true | i) { port in boolean i; }}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // no input port in method call argument in refinement argument of inner component
      arg("import montiarc.test.ComponentTypeWithIntParameter; import montiarc.test.FunctionWithIntParameter; component InvalidComp40 { component Inner refines ComponentTypeWithIntParameter(p = FunctionWithIntParameter(i)) { port in boolean i; } }",
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
