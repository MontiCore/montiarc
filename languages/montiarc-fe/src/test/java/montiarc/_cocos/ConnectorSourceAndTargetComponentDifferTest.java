/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorSourceAndTargetComponentDiffer;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class ConnectorSourceAndTargetComponentDifferTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "connectorSourceAndTargetComponentDiffer";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("ConnectorSourceAndTargetFromSameComponent.arc",
          ArcError.SOURCE_AND_TARGET_SAME_COMPONENT),
      arg("ConnectorSourceAndTargetSamePort.arc",
          ArcError.SOURCE_AND_TARGET_SAME_COMPONENT));
  }

  @ParameterizedTest
  @ValueSource(strings = {"ConnectorSourceAndTargetComponentDiffer.arc", "NoConnectors.arc"})
  public void sourceAndTargetShouldDiffer(@NotNull String model) {
    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldDetectSourceAndTargetAreTheSame(@NotNull String model, @NotNull ArcError... errors) {
    testModel(model, errors);
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new ConnectorSourceAndTargetComponentDiffer());
  }
}