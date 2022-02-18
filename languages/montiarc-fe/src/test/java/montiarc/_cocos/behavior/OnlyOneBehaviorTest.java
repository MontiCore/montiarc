/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcbehaviorbasis.BehaviorError;
import arcbehaviorbasis._cocos.OnlyOneBehavior;
import com.google.common.base.Preconditions;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class OnlyOneBehaviorTest extends AbstractCoCoTest {

  protected final static String PACKAGE = "behavior/onlyOneBehavior";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker).addCoCo(new OnlyOneBehavior());
  }


  protected static Stream<Arguments> modelsAndExpectedErrorProvider() {
    return Stream.of(
      arg("TwoAutomata.arc", BehaviorError.MULTIPLE_BEHAVIOR)
    );
  }

  @ParameterizedTest
  @MethodSource("modelsAndExpectedErrorProvider")
  void shouldFindMultipleBehaviors(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }

  @ParameterizedTest
  @ValueSource(strings = {"NoBehavior.arc", "OneAutomaton.arc"})
  void shouldNotFindMultipleBehaviors(@NotNull String model) {
    testModel(model);
  }
}
