/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.OptionalConfigurationParametersLast;
import com.google.common.base.Preconditions;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
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
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new OptionalConfigurationParametersLast());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "NoParameters.arc",
    "WithMandatoryParameters.arc",
    "WithMandatoryThenOptionalParameters.arc",
    "WithOptionalParameters.arc"
  })
  public void shouldFindCorrectParameterOrderRegardingOptionality(@NotNull String model) {
    testModel(model);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("WithMixedParameters1.arc", ArcError.OPTIONAL_CONFIG_PARAMS_LAST),
      arg("WithMixedParameters2.arc", ArcError.OPTIONAL_CONFIG_PARAMS_LAST),
      arg("WithOptionalThenMandatoryParameters.arc",
          ArcError.OPTIONAL_CONFIG_PARAMS_LAST, ArcError.OPTIONAL_CONFIG_PARAMS_LAST)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldFindMandatoryParametersAfterOptionalOnes(
    @NotNull String model,
    @NotNull  Error... expectedErrors) {
    testModel(model, expectedErrors);
  }
}
