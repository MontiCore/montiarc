/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata.InitialNotFirst.States;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The system under test is the component {@code InitialNotFirst}. The test ensures that the automatons initial
 * state is the expected state.
 */
public class InitialNotFirstTest {

  /**
   * Ensures that the automaton initializes with the correct state, even
   * when it is not named first in the model.
   */
  @Test
  @DisplayName("InitialNotFirst should visit expected states")
  public void shouldInitExpected() {
    // Given
    InitialNotFirst initialNotFirst = new InitialNotFirst();
    initialNotFirst.setUp();
    initialNotFirst.init();

    // When
    States actual = initialNotFirst.getCurrentState();

    // Then
    Assertions.assertEquals(States.B2_2, actual);
  }

}
