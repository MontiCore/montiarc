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
import variablearc._cocos.VarIfIsBoolean;

import java.util.stream.Stream;

import static montiarc.util.VariableArcError.IF_STATEMENT_EXPRESSION_WRONG_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link VarIfIsBoolean}.
 */
class VarIfIsBooleanTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // varif condition with a boolean literal
    """
      component ValidComp1 {
        varif (false) { }
      }
      """
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new VarIfIsBoolean());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new VarIfIsBoolean());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // varif condition with a non-boolean (int) literal
      arg("""
        component InvalidComp1 {
          varif (5) { }
        }
        """,
        IF_STATEMENT_EXPRESSION_WRONG_TYPE
      )
    );
  }
}
