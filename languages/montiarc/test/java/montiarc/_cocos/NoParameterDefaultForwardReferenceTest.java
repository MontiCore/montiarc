/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.NoParameterDefaultForwardReference;
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

import java.util.stream.Stream;

import static montiarc.util.ArcError.PARAMETER_DEFAULT_FORWARD_REFERENCE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoParameterDefaultForwardReference}.
 */
class NoParameterDefaultForwardReferenceTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // parameters without default values
    "component ValidComp1(int a, int b) { }",
    // default value referencing a preceding parameter without default value
    "component ValidComp2(int a, int b = a + 1) { }",
    // default values referencing preceding parameters with default values
    "component ValidComp3(int a = 1, int b = a, int c = a + b) { }",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoParameterDefaultForwardReference());

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
    checker.addCoCo(new NoParameterDefaultForwardReference());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // default value referencing its own parameter
      arg("component InvalidComp1(int a = a + 1) { }", PARAMETER_DEFAULT_FORWARD_REFERENCE),
      // default value referencing a following parameter without cycle
      arg("component InvalidComp2(int a = b, int b = 1) { }", PARAMETER_DEFAULT_FORWARD_REFERENCE),
      // default values referencing each other, only the forward reference is reported
      arg("component InvalidComp3(int a = b, int b = a) { }", PARAMETER_DEFAULT_FORWARD_REFERENCE),
      // each forward reference is reported
      arg("component InvalidComp4(int a = b + c, int b = c, int c = 1) { }",
        PARAMETER_DEFAULT_FORWARD_REFERENCE, PARAMETER_DEFAULT_FORWARD_REFERENCE, PARAMETER_DEFAULT_FORWARD_REFERENCE)
    );
  }
}
