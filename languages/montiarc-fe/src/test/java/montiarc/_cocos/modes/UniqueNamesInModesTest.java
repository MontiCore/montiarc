/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.modes;

import com.google.common.base.Preconditions;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import arcbasis.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static arcbasis.util.ArcError.*;

public class UniqueNamesInModesTest extends AbstractCoCoTest {
  @Override
  protected String getPackage() {
    return "uniqueNamesInModes";
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new basicmodeautomata._cocos.UniqueNamesInModes());
  }

  public static Stream<Arguments> provideErroneousModels() {
    return Stream.of(
        arg("DuplicatedInstanceNameInMode.arc", INSTANCE_NAME_NOT_UNIQUE_IN_MODE),
        arg("DuplicatedInstanceNameInTwoDeclarations.arc", INSTANCE_NAME_NOT_UNIQUE_IN_MODE),
        arg("DuplicatedPortNameInMode.arc", PORT_NAME_NOT_UNIQUE_IN_MODE),
        arg("DuplicatedPortNameInTwoDeclarations.arc", PORT_NAME_NOT_UNIQUE_IN_MODE),
        arg("DuplicatedTypeNameInMode.arc", COMPONENT_NAME_NOT_UNIQUE_IN_MODE),
        arg("DuplicatedTypeNameInTwoDeclarations.arc", COMPONENT_NAME_NOT_UNIQUE_IN_MODE)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "DuplicatedInstanceNameInDifferentModes.arc",
      "DuplicatedPortNameInDifferentModes.arc",
      "DuplicatedTypeNameInDifferentModes.arc"
  })
  public void findNoDuplicatesInErrorlessModels(@NotNull String model) {
    // Given
    Preconditions.checkNotNull(model);

    // When & Then
    this.testModel(model);
  }

  @ParameterizedTest
  @MethodSource("provideErroneousModels")
  public void findErroneouslyDuplicatedNames(@NotNull String model, Error... expectedErrors) {
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