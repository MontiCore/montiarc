/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.modes;

import arcbasis._cocos.PortUniqueSender;
import basicmodeautomata._cocos.StaticCheckOfDynamicTypes;
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

import static montiarc.util.ArcError.PORT_MULTIPLE_SENDER;

public class UsualCoCosAlsoWorkInModesTest extends AbstractCoCoTest {
  @Override
  protected String getPackage() {
    return "usualCoCosAlsoWorkInModes";
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new PortUniqueSender()); // use a simple coco for this test
    checker.addCoCo(new StaticCheckOfDynamicTypes(checker.getTraverser()::getArcBasisVisitorList));
  }

  public static Stream<Arguments> provideErroneousModels() {
    return Stream.of(
        arg("ModeAddsExtraSource.arc", PORT_MULTIPLE_SENDER),
        arg("ModeAddsExtraSourceInNestedComponent.arc", PORT_MULTIPLE_SENDER),
        arg("MultipleSourcesInOneMode.arc", PORT_MULTIPLE_SENDER)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"DifferentSourcesInDifferentModes.arc"})
  public void otherCoCosApproveCorrectModes(@NotNull String model) {
    // Given
    Preconditions.checkNotNull(model);

    // When & Then
    this.testModel(model);
  }

  @ParameterizedTest
  @MethodSource("provideErroneousModels")
  public void otherCoCosFindErrorsInModes(@NotNull String model, Error... expectedErrors) {
    // Given
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(expectedErrors);

    // When
    this.testModel(
        model,
        // Then
        expectedErrors
    );
  }
}