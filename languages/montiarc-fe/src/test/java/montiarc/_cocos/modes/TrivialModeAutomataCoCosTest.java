/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.modes;

import basicmodeautomata._cocos.*;
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

import static arcbasis.util.ArcError.*;

/**
 * Test for all cocos listed in {@link #registerCoCos(MontiArcCoCoChecker)}
 */
public class TrivialModeAutomataCoCosTest extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "reasonableModeAutomata";
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new InitialModeExists());
    checker.addCoCo(new NoHierarchicalModes());
    checker.addCoCo(new NoModesInAtomicComponents());
    checker.addCoCo(new NoModesWithoutAutomata());
    checker.addCoCo(new OneModeAutomatonAtMax());
  }

  public static Stream<Arguments> provideFaultyModels() {
    return Stream.of(
        arg("InitialModeMissing.arc", INITIAL_MODE_DOES_NOT_EXIST),
        arg("HierarchicalMode.arc", HIERARCHICAL_MODE_ELEMENTS, HIERARCHICAL_MODE_ELEMENTS, HIERARCHICAL_MODE_ELEMENTS),
        arg("ModesInNonComposedComponent.arc", MODE_ELEMENTS_IN_ATOMIC_COMPONENTS, MODE_ELEMENTS_IN_ATOMIC_COMPONENTS, MODE_ELEMENTS_IN_ATOMIC_COMPONENTS),
        arg("ModeAutomatonMissing.arc", MODES_WITHOUT_AUTOMATON),
        arg("TwoModeAutomata.arc", MULTIPLE_MODE_AUTOMATA)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"CorrectModeAutomaton.arc"})
  public void processCorrectModels(@NotNull String model) {
    // Given
    Preconditions.checkNotNull(model);

    // When & Then
    this.testModel(model);
  }

  @ParameterizedTest
  @MethodSource("provideFaultyModels")
  public void failOnSimpleFaults(@NotNull String model, Error... expectedErrors) {
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