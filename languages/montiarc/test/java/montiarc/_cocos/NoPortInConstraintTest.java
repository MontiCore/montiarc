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
import variablearc._cocos.NoPortInConstraint;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoPortInConstraint}.
 */
class NoPortInConstraintTest extends MontiArcTestBase {

  final static String SYMBOLS_DIR = "symbols";

  @BeforeEach
  protected void setUp() {
    MontiArcMill.globalScope().setSymbolPath(
      new MCPath(Paths.get(TEST_RESOURCE, SYMBOLS_DIR))
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no ports, no constraint
    "component ValidComp1 { }",
    // ports, no constraint
    "component ValidComp2 { port in int i; port out int o; }",
    // constraint with a literal
    "component ValidComp3 { port in int i; port out int o; constraint(true); }",
    // constraint reading a value from a parameter
    "component ValidComp4(int p) { port in int i; port out int o; constraint(p > 1); }",
    // constraint reading values from a parameter's fields
    "import montiarc.test.OOTypeWithFieldIO; component ValidComp5(OOTypeWithFieldIO p) { port in int i; port out int o; constraint(p.i > 1); constraint(p.o > 1); }",
    // constraint reading values from a parameter's methods
    "import montiarc.test.OOTypeWithFunctionIO; component ValidComp6(OOTypeWithFunctionIO p) { port in int i; port out int o; constraint(p.i() > 1); constraint(p.o() > 1); }",
    // constraint reading values from an external type's static fields
    "import montiarc.test.OOTypeWithStaticFieldIO; component ValidComp7 { port in int i; port out int o; constraint(OOTypeWithStaticFieldIO.i > 1); constraint(OOTypeWithStaticFieldIO.o > 1); }",
    // constraint reading values from an external type's static functions
    "import montiarc.test.OOTypeWithStaticFunctionIO; component ValidComp8 { port in int i; port out int o; constraint(OOTypeWithStaticFunctionIO.i() > 1); constraint(OOTypeWithStaticFunctionIO.o() > 1); }",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoPortInConstraint());

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
    checker.addCoCo(new NoPortInConstraint());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .as(() -> "Findings: " + Log.getFindings().toString())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // input port referenced directly in constraint
      arg("component InvalidComp1 { port in boolean i; constraint(i); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // output port referenced directly in constraint
      arg("component InvalidComp2 { port out boolean o; constraint(o); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in an assignment expression in constraint
      arg("component InvalidComp3 { port out boolean o; constraint(o = true); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in a prefix increment expression in constraint
      arg("component InvalidComp4 { port in int i; constraint(++i); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in a prefix decrement expression in constraint
      arg("component InvalidComp5 { port in int i; constraint(--i); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in a suffix increment expression in constraint
      arg("component InvalidComp6 { port in int i; constraint(i++); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in a suffix decrement expression in constraint
      arg("component InvalidComp7 { port in int i; constraint(i--); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in a boolean-not expression in constraint
      arg("component InvalidComp8 { port in boolean i; constraint(~i); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in a logical-not expression in constraint
      arg("component InvalidComp9 { port in boolean i; constraint(!i); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced on the left of a multiplication in constraint
      arg("component InvalidComp10 { port in int i; constraint(2 == i * 2); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced on the right of a multiplication in constraint
      arg("component InvalidComp11 { port in int i; constraint(2 == 2 * i); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced on both sides of a multiplication in constraint
      arg("component InvalidComp12 { port in int i; constraint(2 == i * i); }",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced on either side of every infix operator in constraint
      arg("""
        component InvalidComp13 {
          port in int i1, i2;
          port in boolean i3, i4;
          constraint(2 == 2 / i2); constraint(2 == i2 / 2);
          constraint(2 == 2 % i2); constraint(2 == i2 % 2);
          constraint(2 == 2 + i2); constraint(2 == i2 + 2);
          constraint(2 == 2 - i2); constraint(2 == i2 - 2);
          constraint(i2 <= 2); constraint(2 <= i2);
          constraint(i2 >= 2); constraint(2 >= i2);
          constraint(i2 < 2); constraint(2 < i2);
          constraint(i2 > 2); constraint(2 > i2);
          constraint(i2 == 2); constraint(2 == i2);
          constraint(i2 != 2); constraint(2 != i2);
          constraint(i4 && 2); constraint(2 && i4);
          constraint(i4 || 2); constraint(2 || i4);
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
      // port referenced in the condition of a conditional expression in constraint
      arg("component InvalidComp14 { port in boolean i; constraint(i ? true : false); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in the then-branch of a conditional expression in constraint
      arg("component InvalidComp15 { port in int i; constraint(true ? i : false); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in the else-branch of a conditional expression in constraint
      arg("component InvalidComp16 { port in int i; constraint(true ? true : i); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced in a bracket expression in constraint
      arg("component InvalidComp17 { port in boolean i; constraint((i)); }",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced on either side of every shift operator in constraint
      arg("""
        component InvalidComp18 {
          port in int i;
          constraint(2 == i << 1);
          constraint(2 == 1 << i);
          constraint(2 == i >> 1);
          constraint(2 == 1 >> i);
          constraint(2 == i >>> 1);
          constraint(2 == 1 >>> i);
        }
        """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced on either side of every binary bitwise/boolean operator in constraint
      arg("""
        component InvalidComp19 {
          port in boolean i;
          constraint(i & true);
          constraint(true & i);
          constraint(i ^ true);
          constraint(true ^ i);
          constraint(i | true);
          constraint(true | i);
        }
        """,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // port referenced as a method call argument in constraint
      arg("""
        import montiarc.test.FunctionWithBooleanParameter;
        component InvalidComp20 {
          port in boolean i;
          constraint(FunctionWithBooleanParameter(i));
        }
        """,
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
