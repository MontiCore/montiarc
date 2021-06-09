/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcautomaton._cocos.OneInitialState;
import arcbehaviorbasis.BehaviorError;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

@Disabled(value = "This coco does not have an error-code yet")
public class InitialStateInAutomata extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "oneInitialStateInAutomata";
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
        Arguments.of("AllStatesInitial.arc", new Error[] {BehaviorError.FIELD_IN_STATECHART_MISSING}),
        Arguments.of("LacksInitialState.arc", new Error[] {BehaviorError.FIELD_IN_STATECHART_MISSING}),
        Arguments.of("TwoInitialStates.arc", new Error[] {BehaviorError.FIELD_IN_STATECHART_MISSING})
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "NoStatechart.arc",
      "HasInitialState.arc"})
  public void succeed(@NotNull String model) {
    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void fail(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new OneInitialState());
  }

}