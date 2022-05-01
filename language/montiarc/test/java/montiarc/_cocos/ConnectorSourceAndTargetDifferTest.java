/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorSourceAndTargetDiffer;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class ConnectorSourceAndTargetDifferTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "connectorSourceAndTargetDiffer";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("ConnectorSourceAndTargetSamePort.arc",
          ArcError.CONNECTOR_SOURCE_AND_TARGET_ARE_IDENTICAL));
  }

  @ParameterizedTest
  @ValueSource(strings = {"ConnectorSourceAndTargetFromSameComponent.arc",
          "ConnectorSourceAndTargetComponentDiffer.arc", "NoConnectors.arc"})
  public void sourceAndTargetShouldDiffer(@NotNull String model) {
    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldDetectSourceAndTargetAreTheSame(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new ConnectorSourceAndTargetDiffer());
  }
}