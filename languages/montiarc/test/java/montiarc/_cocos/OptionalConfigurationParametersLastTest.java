/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.OptionalConfigurationParametersLast;
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

import static montiarc.util.ArcError.OPTIONAL_PARAMS_LAST;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link OptionalConfigurationParametersLast}.
 */
class OptionalConfigurationParametersLastTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // no parameters
    "component ValidComp1 { }",
    // one mandatory parameter
    "component ValidComp2(int p) { }",
    // two mandatory parameters
    "component ValidComp3(int p1, int p2) { }",
    // one optional parameter
    "component ValidComp4(int p = 1) { }",
    // two optional parameters
    "component ValidComp5(int p1 = 1, int p2 = 2) { }",
    // one mandatory and one optional parameter
    "component ValidComp6(int p1, int p2 = 2) { }",
    // two mandatory and one optional parameter
    "component ValidComp7(int p1, int p2, int p3 = 3) { }",
    // one mandatory and two optional parameters
    "component ValidComp8(int p1, int p2 = 2, int p3 = 3) { }",
    // two mandatory and two optional parameters
    "component ValidComp9(int p1, int p2, int p3 = 3, int p4 = 4) { }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new OptionalConfigurationParametersLast());

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
    checker.addCoCo(new OptionalConfigurationParametersLast());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // one optional parameter followed by one mandatory parameter
      arg("component InvalidComp1(int p2 = 1, int p1) { }",
        OPTIONAL_PARAMS_LAST),
      // two optional parameters followed by one mandatory parameter
      arg("component InvalidComp2(int p3 = 1, int p2 = 2, int p1) { }",
        OPTIONAL_PARAMS_LAST),
      // mandatory, optional, then mandatory parameter
      arg("component InvalidComp3(int p1, int p3 = 2, int p2) { }",
        OPTIONAL_PARAMS_LAST),
      // mandatory and optional parameters intertwined
      arg("component InvalidComp4(int p1, int p3 = 2, int p2, int p4 = 4) { }",
        OPTIONAL_PARAMS_LAST)
    );
  }
}
