/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.CheckNoFieldDependencyCycles;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integrationstest for the CoCo {@link CheckNoFieldDependencyCycles}.
 */
public class CheckNoFieldDependencyCyclesTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 { int a = 1; int b = a + 1; }",
    "component Comp2 { int x = 0; }"
  })
  public void shouldNotReportError(String model) throws IOException {
    Preconditions.checkNotNull(model);
    ASTMACompilationUnit ast = compile(model);
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new CheckNoFieldDependencyCycles());
    checker.checkAll(ast);
    assertThat(Log.getFindingsCount())
      .as(Log.getFindings().toString())
      .isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(String model, Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    ASTMACompilationUnit ast = compile(model);
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new CheckNoFieldDependencyCycles());
    checker.checkAll(ast);
    assertThat(Log.getFindingsCount())
      .as(Log.getFindings().toString())
      .isEqualTo(errors.length);
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg(
        "component Comp1 { int a = b; int b = a; }",
        ArcError.CIRCULAR_FIELDS_DEPENDENCY
      ),
      arg(
        "component Comp2 { int a = c; int b = a; int c = b; }",
        ArcError.CIRCULAR_FIELDS_DEPENDENCY
      ),
      arg(
        "component Comp3 { int a = 1 + b; int b = 2 + a; }",
        ArcError.CIRCULAR_FIELDS_DEPENDENCY
      )
    );
  }
}
