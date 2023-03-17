/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import de.monticore.scbasis._cocos.AtLeastOneInitialState;
import de.monticore.scbasis._cocos.MaxOneInitialState;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.Error;
import montiarc.util.MCError;
import montiarc.util.MontiArcError;
import montiarc.util.SCError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class InitialStateNumberInAutomataTest extends AbstractCoCoTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    String montiArcError = MontiArcError.ERROR_CODE_PATTERN.pattern();
    String mcError = MCError.ERROR_CODE_PATTERN.pattern();
    String scError = SCError.ERROR_CODE_PATTERN.pattern();
    return Pattern.compile(montiArcError + "|" + mcError + "|" + scError);
  }

  @Override
  protected String getPackage() {
    return "behavior/oneInitialStateInAutomata";
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
        Arguments.of("AllStatesInitial.arc", new Error[] { SCError.MORE_THAN_ONE_INITIAL_STATE }),
        Arguments.of("LacksInitialState.arc", new Error[] { SCError.NO_INITIAL_STATE }),
        Arguments.of("TwoInitialStates.arc", new Error[] { SCError.MORE_THAN_ONE_INITIAL_STATE }),
        Arguments.of("MultipleInitialOutputDeclarations.arc", new Error[] { SCError.MORE_THAN_ONE_INITIAL_STATE }),
        Arguments.of("MultipleStatesDeclaredInitial.arc", new Error[] { SCError.MORE_THAN_ONE_INITIAL_STATE }),
        Arguments.of("RedundantInitialOutputDeclarations.arc", new Error[] { SCError.MORE_THAN_ONE_INITIAL_STATE })
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "NoStatechart.arc",
      "HasInitialState.arc",
      "StateDeclaredInitialTwice.arc",
      "StateDeclaredInitialSeparately.arc"})
  void succeed(@NotNull String model) {
    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  void fail(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new AtLeastOneInitialState());
    checker.addCoCo(new MaxOneInitialState());
  }
}