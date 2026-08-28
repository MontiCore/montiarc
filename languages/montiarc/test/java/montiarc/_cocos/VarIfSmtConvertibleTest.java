/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.VarIfSmtConvertible;

import java.util.stream.Stream;

import static montiarc.util.VariableArcError.EXPRESSION_NOT_SMT_CONVERTIBLE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link VarIfSmtConvertible}.
 */
class VarIfSmtConvertibleTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // component without a varif
    "component ValidComp1 { }",
    // varif condition referencing a feature
    "component ValidComp2 { feature f; varif (f) { } }",
    // varif condition negating a feature
    "component ValidComp3 { feature f; varif (!f) { } }",
    // varif condition conjunction of a feature and its negation
    "component ValidComp4 { feature f; varif (f && !f) { } }",
    // varif condition disjunction of a feature and its negation
    "component ValidComp5 { feature f; varif (f || !f) { } }",
    // varif condition comparing an int parameter
    "component ValidComp6(int p) { varif (p > 0) { } }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new VarIfSmtConvertible());

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
    checker.addCoCo(new VarIfSmtConvertible());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // varif condition containing a function call
      arg("""
        import java.lang.Math;
        component InvalidComp1 {
          varif (Math.abs(1) < 0) { }
        }
        """,
        EXPRESSION_NOT_SMT_CONVERTIBLE
      )
    );
  }
}
