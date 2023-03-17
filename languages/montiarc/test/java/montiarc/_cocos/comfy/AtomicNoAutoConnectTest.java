/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.comfy;

import com.google.common.base.Preconditions;
import comfortablearc._cocos.AtomicNoAutoConnect;
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

public class AtomicNoAutoConnectTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "comfy/noAutoconnectInAtomicComponents";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker).addCoCo(new AtomicNoAutoConnect());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AutoconnectForComposition.arc",
    "NoAutoconnectForAtomic.arc",
    "NoAutoconnectForComposition.arc"})
  void shouldAllowNoAutoconnectDeclarationOrDeclarationsForCompositions(@NotNull String model) {
    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  void shouldViolateAutoconnectsForAtomicComponents(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    testModel(model, errors);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    Arguments arg = arg("AutoconnectForAtomic.arc",
      ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT, ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT,
      ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT);

    return Stream.of(arg
    );
  }
}
