/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.NoPortInFieldDeclaration;
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
 * The class under test is {@link NoPortInFieldDeclaration}.
 */
class NoPortInFieldDeclarationTest extends MontiArcTestBase {

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
    checker.addCoCo(new NoPortInFieldDeclaration());

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
    checker.addCoCo(new NoPortInFieldDeclaration());

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
      // component field declaration with literal
      arg("""
        component ValidComp3 {
          port in int i;
          port out int o;
          int v = 1;
        }
        """
      ),
      // component field declaration, parameter
      arg("""
        component ValidComp4(int p) {
          port in int i;
          port out int o;
          int v = p;
        }
        """
      ),
      // component field declaration, field
      arg("""
        import montiarc.test.OOTypeWithFieldIO;
        component ValidComp5(OOTypeWithFieldIO p) {
          port in int i;
          port out int o;
          int v1 = p.i;
          int v2 = p.o;
        }
        """
      ),
      // component field declaration, function
      arg("""
        import montiarc.test.OOTypeWithFunctionIO;
        component ValidComp6(OOTypeWithFunctionIO p) {
          port in int i;
          port out int o;
          int v1 = p.i();
          int v2 = p.o();
        }
        """
      ),
      // component field declaration, static field
      arg("""
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp7 {
          port in int i;
          port out int o;
          int v1 = OOTypeWithStaticFieldIO.i;
          int v2 = OOTypeWithStaticFieldIO.o;
        }
        """
      ),
      // component field declaration, static function
      arg("""
        import montiarc.test.OOTypeWithStaticFunctionIO;
        component ValidComp8 {
          port in int i;
          port out int o;
          int v1 = OOTypeWithStaticFunctionIO.i();
          int v2 = OOTypeWithStaticFunctionIO.o();
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // input port in field declaration
      arg("""
          component InvalidComp1 {
            port in int i;
            int v = i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // output port in field declaration
      arg("""
          component InvalidComp2 {
            port out int o;
            int v = o;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in assignment in field declaration
      arg("""
          component InvalidComp3 {
            port in int i;
            int v = i = 1;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in inc prefix expression in field declaration
      arg("""
          component InvalidComp4 {
            port in int i;
            int v = ++i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in dec prefix expression in field declaration
      arg("""
          component InvalidComp5 {
            port in int i;
            int v = --i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in inc suffix expression in field declaration
      arg("""
          component InvalidComp6 {
            port in int i;
            int v = i++;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in dec suffix expression in field declaration
      arg("""
          component InvalidComp7 {
            port in int i;
            int v = i--;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in boolean not expression in field declaration
      arg("""
          component InvalidComp8 {
            port in boolean i;
            boolean v = ~i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in logical not expression in field declaration
      arg("""
          component InvalidComp9 {
            port in boolean i;
            boolean v = !i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in multiply expressions (left) in field declaration
      arg("""
          component InvalidComp10 {
            port in int i;
            int v = i * 2;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in multiply expressions (right) in field declaration
      arg("""
          component InvalidComp11 {
            port in int i;
            int v = 2 * i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in multiply expressions (both) in field declaration
      arg("""
          component InvalidComp12 {
            port in int i;
            int v = i * i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in infix expressions in field declaration
      arg("""
          import montiarc.test.ComponentTypeWithBooleanParameter;
          component InvalidComp13 {
            port in int i1, i2;
            port in boolean i3, i4;
            int v1 = 2 / i2;
            int v2 = i2 / 2;
            int v3 = 2 % i2;
            int v4 = i2 % 2;
            int v5 = 2 + i2;
            int v6 = i2 + 2;
            int v7 = 2 - i2;
            int v8 = i2 - 2;
            boolean v9 = i2 <= 2;
            boolean v10 = 2 <= i2;
            boolean v11 = i2 >= 2;
            boolean v12 = 2 >= i2;
            boolean v13 = i2 < 2;
            boolean v14 = 2 < i2;
            boolean v15 = i2 > 2;
            boolean v16 = 2 > i2;
            boolean v17 = i2 == 2;
            boolean v18 = 2 == i2;
            boolean v19 = i2 != 2;
            boolean v20 = 2 != i2;
            boolean v21 = i4 && 2;
            boolean v22 = 2 && i4;
            boolean v23 = i4 || 2;
            boolean v24 = 2 || i4;
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
      // input port in conditional expressions (condition) in field declaration
      arg("""
          component InvalidComp14 {
            port in boolean i;
            int v = i ? -2 : 2;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in conditional expressions (then) in field declaration
      arg("""
          component InvalidComp15 {
            port in int i;
            int v = true ? i : 2;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in conditional expressions (else) in field declaration
      arg("""
          component InvalidComp16 {
            port in int i;
            int v = true ? -2 : i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in bracket expressions in field declaration
      arg("""
          component InvalidComp17 {
            port in int i;
            int v = (i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in shift expressions in field declaration
      arg("""
          component InvalidComp18 {
            port in int i;
            int v1 = i << 1;
            int v2 = 1 << i;
            int v3 = i >> 1;
            int v4 = 1 >> i;
            int v5 = i >>> 1;
            int v6 = 1 >>> i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in binary expressions in field declaration
      arg("""
          component InvalidComp19 {
            port in boolean i;
            boolean v1 = i & true;
            boolean v2 = true & i;
            boolean v3 = i ^ true;
            boolean v4 = true ^ i;
            boolean v5 = i | true;
            boolean v6 = true | i;
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // input port in method call argument in field declaration
      arg("""
          import montiarc.test.FunctionWithIntParameter;
          component InvalidComp20 {
            port in int i;
            int v = FunctionWithIntParameter(i);
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
