/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcautomaton._cocos.OneInitialStateAtMax;
import montiarc.util.ArcError;
import de.monticore.scbasis._cocos.AtLeastOneInitialState;
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
        Arguments.of("AllStatesInitial.arc", new Error[] {ArcError.MANY_INITIAL_STATES}),
        // Arguments.of("LacksInitialState.arc", new Error[] { /* Error for missing initial state. */ }),  Comes from a statechart language, and thus it is hard to test here, and we already test it in OriginalStatechartCocos
        Arguments.of("TwoInitialStates.arc", new Error[] {ArcError.MANY_INITIAL_STATES}),
        Arguments.of("MultipleInitialOutputDeclarations.arc", new Error[] {ArcError.MANY_INITIAL_STATES}),
        Arguments.of("MultipleStatesDeclaredInitial.arc", new Error[] {ArcError.MANY_INITIAL_STATES}),
        Arguments.of("RedundantInitialOutputDeclarations.arc", new Error[] {ArcError.MANY_INITIAL_STATES})
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
    checker.addCoCo(new AtLeastOneInitialState());
    checker.addCoCo(new OneInitialStateAtMax());
  }
}