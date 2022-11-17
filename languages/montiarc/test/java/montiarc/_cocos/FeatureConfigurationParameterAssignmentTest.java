/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import montiarc.check.MontiArcTypeCalculator;
import montiarc.util.Error;
import montiarc.util.VariableArcError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._cocos.FeatureConfigurationParameterAssignment;

import java.util.stream.Stream;

public class FeatureConfigurationParameterAssignmentTest extends AbstractCoCoTest {

  protected static Stream<Arguments> provideFileAndErrors() {
    return Stream.of(
      Arguments.of(
        "WithCorrectAssignment.arc",
        new Error[]{}
      ),
      Arguments.of(
        "IncompatibleBindings.arc",
        new Error[]{
          VariableArcError.NAMED_ARGUMENT_EXPRESSION_WRONG_TYPE
        }
      ),
      Arguments.of(
        "NotExistingFeature.arc",
        new Error[]{
          VariableArcError.NAMED_ARGUMENT_FEATURE_NOT_EXIST
        }
      ),
      Arguments.of(
        "TooManyFeatureAssignments.arc",
        new Error[]{
          VariableArcError.TOO_MANY_INSTANTIATION_FEATURE_ARGUMENTS,
          VariableArcError.NAMED_ARGUMENT_FEATURE_NOT_EXIST,
          VariableArcError.TOO_MANY_INSTANTIATION_FEATURE_ARGUMENTS,
          VariableArcError.NAMED_ARGUMENT_FEATURE_NOT_EXIST
        }
      ),
      Arguments.of(
        "WrongAssignmentStatement.arc",
        new Error[]{
          VariableArcError.NAMED_ARGUMENT_NO_NAME_LEFT,
          VariableArcError.NAMED_ARGUMENT_NO_NAME_LEFT
        }
      )
    );
  }

  public void loadComponentsToInstantiate() {
    // loading helper models into the symboltable
    this.parseAndCreateAndCompleteSymbols("subcomponentDefinitions/Simple.arc");
    this.parseAndCreateAndCompleteSymbols("subcomponentDefinitions/Complex.arc");
  }

  @BeforeEach
  public void prepareModels() {
    loadComponentsToInstantiate();
  }

  @ParameterizedTest
  @MethodSource("provideFileAndErrors")
  public void testFile(String file, Error[] errorList) {
    testModel(file, errorList);
  }

  @Override
  protected String getPackage() {
    return "featureConfigurationParameterAssignment";
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new FeatureConfigurationParameterAssignment(new MontiArcTypeCalculator()));
  }
}
