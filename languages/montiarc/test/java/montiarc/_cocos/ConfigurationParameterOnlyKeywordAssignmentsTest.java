/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

class ConfigurationParameterOnlyKeywordAssignmentsTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "configurationParameterAssignment";

  @BeforeEach
  public void prepareModels() {
    loadComponentsToInstantiate();
  }

  public void loadComponentsToInstantiate() {
    // loading helper models into the symboltable
    this.parseAndCreateAndCompleteSymbols("subcomponentDefinitions/Simple.arc");
    this.parseAndCreateAndCompleteSymbols("subcomponentDefinitions/Complex.arc");
  }

  @ParameterizedTest
  @ValueSource(strings = {"CorrectParameterBindings.arc", "CorrectGenericParameterBindings.arc"})
  void shouldCorrectlyBindConfigurationParameters(@NotNull String model) {
    testModel(model);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("InvalidAssignmentSyntax.arc",
        ArcError.INSTANTIATION_ARGUMENT_INVALID_ASSIGNMENT,
        ArcError.INSTANTIATION_ARGUMENT_INVALID_ASSIGNMENT)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  void shouldIncorrectlyBindConfigurationParameters(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    testModel(model, errors);
  }

  @Override
  protected String getPackage() { return PACKAGE; }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new ConfigurationParameterOnlyKeywordAssignments());
  }
}