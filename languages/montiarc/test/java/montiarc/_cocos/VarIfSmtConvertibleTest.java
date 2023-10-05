/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.VariableArcError;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.VarIfSmtConvertible;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Tests for {@link VarIfSmtConvertible}
 */
public class VarIfSmtConvertibleTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 { }",
    "component Comp2 { feature f; varif (f) { } }",
    "component Comp3 { feature f; varif (!f) { } }",
    "component Comp4 { feature f; varif (f && !f) { } }",
    "component Comp5 { feature f; varif (f || !f) { } }",
    "component Comp6(int p) { varif (p > 0) { } }"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new VarIfSmtConvertible());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new VarIfSmtConvertible());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("import java.lang.Math; component Comp1 { varif (Math.abs(1) < 0) { } }", VariableArcError.EXPRESSION_NOT_SMT_CONVERTIBLE)
    );
  }
}
