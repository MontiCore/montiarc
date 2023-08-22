/* (c) https://github.com/MontiCore/monticore */
package modes._cocos;

import com.google.common.base.Preconditions;
import modes.ModesAbstractTest;
import modes.ModesMill;
import modes._ast.ASTModeAutomaton;
import montiarc.util.ModesError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Tests for {@link ModeAutomatonContainsNoStates}
 */
public class ModeAutomatonContainsNoStatesTest extends ModesAbstractTest {

  protected static Stream<Arguments> numberOfModesAndStatesWithErrorProvider() {
    return Stream.of(
      Arguments.of(0, 0, new ModesError[]{}),
      Arguments.of(1, 0, new ModesError[]{}),
      Arguments.of(2, 0, new ModesError[]{}),
      Arguments.of(3, 0, new ModesError[]{}),
      Arguments.of(0, 1, new ModesError[]{ModesError.MODE_AUTOMATON_CONTAINS_STATE}),
      Arguments.of(1, 1, new ModesError[]{ModesError.MODE_AUTOMATON_CONTAINS_STATE}),
      Arguments.of(2, 1, new ModesError[]{ModesError.MODE_AUTOMATON_CONTAINS_STATE}),
      Arguments.of(3, 1, new ModesError[]{ModesError.MODE_AUTOMATON_CONTAINS_STATE}),
      Arguments.of(0, 2, new ModesError[]{ModesError.MODE_AUTOMATON_CONTAINS_STATE}),
      Arguments.of(1, 2, new ModesError[]{ModesError.MODE_AUTOMATON_CONTAINS_STATE})
    );
  }

  @ParameterizedTest
  @MethodSource("numberOfModesAndStatesWithErrorProvider")
  public void testCocoWithNModeAutomata(int numberOfModes, int numberOfStates, @NotNull ModesError... expectedErrors) {
    Preconditions.checkArgument(numberOfModes >= 0);
    Preconditions.checkArgument(numberOfStates >= 0);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    ASTModeAutomaton modeAutomaton = ModesMill.modeAutomatonBuilder().build();
    modeAutomaton.setSpannedScope(ModesMill.scope());
    for (int i = 0; i < numberOfModes; i++) {
      modeAutomaton.getSpannedScope().add(ModesMill.arcModeSymbolBuilder().setName("m" + i).build());
    }
    for (int i = 0; i < numberOfStates; i++) {
      modeAutomaton.getSpannedScope().add(ModesMill.sCStateSymbolBuilder().setName("s" + i).build());
    }

    // When
    ModeAutomatonContainsNoStates coco = new ModeAutomatonContainsNoStates();
    coco.check(modeAutomaton);

    // Then
    checkOnlyExpectedErrorsPresent(expectedErrors);
  }
}
