/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.OptionalConfigurationParametersLast;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class OptionalConfigurationParametersLastTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "optionalConfigurationParametersLast";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos() { this.getChecker().addCoCo(new OptionalConfigurationParametersLast()); }

  @ParameterizedTest
  @ValueSource(strings = {
    "NoParameters.arc",
    "WithMandatoryParameters.arc",
    "WithMandatoryThenOptionalParameters.arc",
    "WithOptionalParameters.arc"
  })
  public void shouldFindCorrectParameterOrderRegardingOptionality(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("WithMixedParameters1.arc", ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH),
      arg("WithMixedParameters2.arc", ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH),
      arg("WithOptionalThenMandatoryParameters.arc",
          ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH, ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldFindMandatoryParametersAfterOptionalOnes(
    @NotNull String model,
    @NotNull  ArcError... expectedErrors) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //then
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), expectedErrors);
  }
}
