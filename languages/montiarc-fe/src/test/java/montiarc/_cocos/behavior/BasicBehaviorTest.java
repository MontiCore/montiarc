/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcbehaviorbasis.BehaviorError;
import arcbehaviorbasis._cocos.NoBehaviorInComposedComponents;
import arcbehaviorbasis._cocos.OnlyOneBehavior;
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

/**
 * Tests which components may have which amount of behavior
 */
@Disabled(value = "These cocos do not have error-codes yet")
public class BasicBehaviorTest extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "behavior";
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      Arguments.of("ComposedComponentWithBehavior.arc", new Error[] {BehaviorError.BEHAVIOR_IN_COMPOSED_COMPONENT}),
      Arguments.of("TwoAutomata.arc", new Error[] {BehaviorError.MULTIPLE_BEHAVIOR})
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "ComposedComponentWithoutBehavior.arc",
      "NoBehavior.arc",
      "OneAutomaton.arc"
  })
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
    checker.addCoCo(new OnlyOneBehavior());
    checker.addCoCo(new NoBehaviorInComposedComponents());
  }
}