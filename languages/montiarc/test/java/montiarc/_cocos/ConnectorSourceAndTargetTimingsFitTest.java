/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorSourceAndTargetTimingsFit;
import com.google.common.base.Preconditions;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ConnectorSourceAndTargetTimingsFitTest extends AbstractCoCoTest {

  /**
   * This method that facilitates stating arguments for parameterized tests. By using an elliptical parameter this
   * method removes the need to explicitly create arrays.
   *
   * @param model  model to test
   * @param errors all expected errors
   */
  protected static Arguments arg(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    return Arguments.of(model, errors);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
        arg("SourceAndTargetTimingsFit.arc"),
        arg("SourceAndTargetTimingsMismatch.arc",
            ArcError.SOURCE_AND_TARGET_TIMING_MISMATCH, ArcError.SOURCE_AND_TARGET_TIMING_MISMATCH,
            ArcError.SOURCE_AND_TARGET_TIMING_MISMATCH, ArcError.SOURCE_AND_TARGET_TIMING_MISMATCH
        )
    );
  }

  @Override
  protected String getPackage() {
    return "connectorSourceAndTargetTimingsFit";
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new ConnectorSourceAndTargetTimingsFit());
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldFind(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    testModel(model, errors);
  }
}
