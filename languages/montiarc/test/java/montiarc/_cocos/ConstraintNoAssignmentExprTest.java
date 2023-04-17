/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.ConstraintNoAssignmentExpr;

import java.io.IOException;
import java.util.stream.Stream;

public class ConstraintNoAssignmentExprTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 { }",
    "component Comp2 { feature f; constraint(f); }",
    "component Comp3 { feature f; constraint(!f); }",
    "component Comp4 { feature f; constraint(f && !f); }",
    "component Comp5 { feature f; constraint(f || !f); }",
    "component Comp6(int p) { constraint(p > 0); }"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConstraintNoAssignmentExpr());

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
    checker.addCoCo(new ConstraintNoAssignmentExpr());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component Comp1 { feature f; constraint(f = true); }", ArcError.INVALID_CONTEXT_ASSIGNMENT),
      arg("component Comp2 { feature f; constraint(f &= true); }", ArcError.INVALID_CONTEXT_ASSIGNMENT),
      arg("component Comp3 { feature f; constraint(f |= true); }", ArcError.INVALID_CONTEXT_ASSIGNMENT),
      arg("component Comp4 { feature f; constraint(f ^= true); }", ArcError.INVALID_CONTEXT_ASSIGNMENT),
      arg("component Comp5(int p) { constraint((p += 0) > 1); }", ArcError.INVALID_CONTEXT_ASSIGNMENT),
      arg("component Comp6(int p) { constraint((p -= 0) > 1); }", ArcError.INVALID_CONTEXT_ASSIGNMENT),
      arg("component Comp7(int p) { constraint((p *= 0) > 1); }", ArcError.INVALID_CONTEXT_ASSIGNMENT),
      arg("component Comp8(int p) { constraint((p /= 0) > 1); }", ArcError.INVALID_CONTEXT_ASSIGNMENT),
      arg("component Comp9(int p) { constraint((p %= 0) > 1); }", ArcError.INVALID_CONTEXT_ASSIGNMENT),
      arg("component Comp10(int p) { constraint((p >>= 1) > 0); }", ArcError.INVALID_CONTEXT_ASSIGNMENT),
      arg("component Comp11(int p) { constraint((p >>>= 1) > 0); }", ArcError.INVALID_CONTEXT_ASSIGNMENT),
      arg("component Comp12(int p) { constraint((p <<= 1) > 0); }", ArcError.INVALID_CONTEXT_ASSIGNMENT),
      arg("component Comp13(int p) { constraint(++p > 0); }", ArcError.INVALID_CONTEXT_INC_PREFIX),
      arg("component Comp14(int p) { constraint(--p > 0); }", ArcError.INVALID_CONTEXT_DEC_PREFIX),
      arg("component Comp15(int p) { constraint(p++ > 0); }", ArcError.INVALID_CONTEXT_INC_SUFFIX),
      arg("component Comp16(int p) { constraint(p-- > 0); }", ArcError.INVALID_CONTEXT_DEC_SUFFIX)
    );
  }
}
