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
import variablearc._cocos.ConstraintSmtConvertible;

import java.util.stream.Stream;

import static montiarc.util.VariableArcError.EXPRESSION_NOT_SMT_CONVERTIBLE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConstraintSmtConvertible}.
 */
class ConstraintSmtConvertibleTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // component without a constraint
    "component ValidComp1 { }",
    // constraint referencing a feature
    "component ValidComp2 { feature f; constraint(f); }",
    // constraint negating a feature
    "component ValidComp3 { feature f; constraint(!f); }",
    // constraint conjunction of a feature and its negation
    "component ValidComp4 { feature f; constraint(f && !f); }",
    // constraint disjunction of a feature and its negation
    "component ValidComp5 { feature f; constraint(f || !f); }",
    // constraint comparing an int parameter
    "component ValidComp6(int p) { constraint(p > 0); }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConstraintSmtConvertible());

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
    checker.addCoCo(new ConstraintSmtConvertible());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // constraint referencing a non-boolean (String) parameter
      arg("""
        component InvalidComp1(String p) {
          constraint(p);
        }
        """,
        EXPRESSION_NOT_SMT_CONVERTIBLE
      )
    );
  }
}
