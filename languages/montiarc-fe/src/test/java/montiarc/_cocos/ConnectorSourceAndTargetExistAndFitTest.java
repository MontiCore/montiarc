/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorSourceAndTargetExistAndFit;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;
import java.util.stream.Stream;

public class ConnectorSourceAndTargetExistAndFitTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "connectorSourceAndTargetExistAndFit";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("EverythingMismatched.arc",
        ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH,
        ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH,
        ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH,
        ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH
        ),
      arg("SourceAndTargetDoNotExist.arc",
        ArcError.SOURCE_PORT_NOT_EXISTS, ArcError.TARGET_PORT_NOT_EXISTS,
        ArcError.SOURCE_PORT_NOT_EXISTS, ArcError.TARGET_PORT_NOT_EXISTS,
        ArcError.SOURCE_PORT_NOT_EXISTS, ArcError.TARGET_PORT_NOT_EXISTS,
        ArcError.SOURCE_PORT_NOT_EXISTS, ArcError.TARGET_PORT_NOT_EXISTS
      ),
      arg("SourceAndTargetTypeMismatch.arc",
        ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH, ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH,
        ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH, ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH
      ),
      arg("SourceAndTargetWrongDirection.arc",
        ArcError.PORT_DIRECTION_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH,
        ArcError.PORT_DIRECTION_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH,
        ArcError.PORT_DIRECTION_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH,
        ArcError.PORT_DIRECTION_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH
      ),
      arg("SourceDoesNotExist.arc",
        ArcError.SOURCE_PORT_NOT_EXISTS, ArcError.SOURCE_PORT_NOT_EXISTS,
        ArcError.SOURCE_PORT_NOT_EXISTS, ArcError.SOURCE_PORT_NOT_EXISTS
      ),
      arg("SourceWrongDirection.arc",
        ArcError.PORT_DIRECTION_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH,
        ArcError.PORT_DIRECTION_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH
      ),
      arg("TargetDoesNotExist.arc",
        ArcError.TARGET_PORT_NOT_EXISTS, ArcError.TARGET_PORT_NOT_EXISTS,
        ArcError.TARGET_PORT_NOT_EXISTS, ArcError.TARGET_PORT_NOT_EXISTS
      ),
      arg("TargetWrongDirection.arc",
        ArcError.PORT_DIRECTION_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH,
        ArcError.PORT_DIRECTION_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH
      )
    );
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new ConnectorSourceAndTargetExistAndFit());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AllSourcesAndTargetsExistAndFit.arc",
    "NoConnectors.arc"})
  public void connectorSourceAndTargetShouldFit(@NotNull String model) {
    Preconditions.checkNotNull(model);
    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldFind(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    testModel(model, errors);
  }

  @Override
  @BeforeEach
  public void init() {
    super.init();
    this.getTool().initializeBasicTypes();
    this.getTool().parseAll(Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH, getPackage()))
      .forEach(ast -> this.getTool().createSymbolTable(ast));
  }
}