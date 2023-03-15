/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.comfy;

import com.google.common.base.Preconditions;
import comfortablearc._cocos.MaxOneAutoConnect;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.ComfortableArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

class MaxOneAutoConnectTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "comfy/maxOneAutoconnectPerComponent";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker).addCoCo(new MaxOneAutoConnect());
  }

  @ParameterizedTest
  @ValueSource(strings = {"OneOrNoAutoconnect.arc"})
  void shouldAllowNoOrSingleAutoconnectDeclarations(@NotNull String model) {
    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  void shouldViolateMultipleAutoconnectDeclarations(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    testModel(model, errors);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("MultipleAutoconnects.arc",
        ComfortableArcError.MULTIPLE_AUTOCONNECTS, ComfortableArcError.MULTIPLE_AUTOCONNECTS,
        ComfortableArcError.MULTIPLE_AUTOCONNECTS, ComfortableArcError.MULTIPLE_AUTOCONNECTS,
        ComfortableArcError.MULTIPLE_AUTOCONNECTS, ComfortableArcError.MULTIPLE_AUTOCONNECTS,
        ComfortableArcError.MULTIPLE_AUTOCONNECTS, ComfortableArcError.MULTIPLE_AUTOCONNECTS,
        ComfortableArcError.MULTIPLE_AUTOCONNECTS, ComfortableArcError.MULTIPLE_AUTOCONNECTS,
        ComfortableArcError.MULTIPLE_AUTOCONNECTS
      )
    );
  }
}
