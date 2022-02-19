/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcautomaton._cocos.InitialStatesResolvable;
import arcautomaton._cocos.NoRedundantInitialOutput;
import arcautomaton._cocos.OneInitialStateAtLeast;
import arcautomaton._cocos.OneInitialStateAtMax;
import arcbehaviorbasis.BehaviorError;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class InitialStateInAutomata extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "behavior/oneInitialStateInAutomata";
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
        Arguments.of("StateMissing.arc", new Error[] {BehaviorError.INITIAL_STATE_REFERENCE_MISSING}),
        Arguments.of("AllStatesInitial.arc", new Error[] {BehaviorError.MANY_INITIAL_STATES}),
        Arguments.of("LacksInitialState.arc", new Error[] {BehaviorError.NO_INITIAL_STATE}),
        Arguments.of("TwoInitialStates.arc", new Error[] {BehaviorError.MANY_INITIAL_STATES}),
        Arguments.of("MultipleInitialOutputDeclarations.arc", new Error[] {BehaviorError.MANY_INITIAL_STATES}),
        Arguments.of("MultipleStatesDeclaredInitial.arc", new Error[] {BehaviorError.MANY_INITIAL_STATES}),
        Arguments.of("RedundantInitialOutputDeclarations.arc", new Error[] {BehaviorError.REDUNDANT_INITIAL_DECLARATION})
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "NoStatechart.arc",
      "HasInitialState.arc",
      "StateDeclaredInitialTwice.arc",
      "StateDeclaredInitialSeparately.arc"})
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
    checker.addCoCo(new OneInitialStateAtLeast());
    checker.addCoCo(new OneInitialStateAtMax());
    checker.addCoCo(new NoRedundantInitialOutput());
    checker.addCoCo(new InitialStatesResolvable());
  }

}