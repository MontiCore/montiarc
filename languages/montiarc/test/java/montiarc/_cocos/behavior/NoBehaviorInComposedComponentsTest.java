/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcbasis._cocos.NoBehaviorInComposedComponents;
import com.google.common.base.Preconditions;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class NoBehaviorInComposedComponentsTest extends AbstractCoCoTest {

  protected final static String PACKAGE = "behavior/noBehaviorInComposedComponents";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker).addCoCo(new NoBehaviorInComposedComponents());
  }

  protected static Stream<Arguments> modelsAndExpectedErrorProvider() {
    return Stream.of(
      arg("ComposedComponentWithBehavior.arc", ArcError.BEHAVIOR_IN_COMPOSED_COMPONENT)
    );
  }

  @ParameterizedTest
  @MethodSource("modelsAndExpectedErrorProvider")
  void shouldFindBehaviorInComposedComponents(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AtomicWithBehavior.arc", "AtomicWithoutBehavior.arc","ComposedComponentWithoutBehavior.arc"})
  void shouldNotFindBehaviorInComposedComponents(@NotNull String model) {
    testModel(model);
  }
}
