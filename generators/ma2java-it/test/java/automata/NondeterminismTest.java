/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata.NondeterminismImpl.NondeterminismState;
import com.google.common.base.Preconditions;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The system under test is the component {@code Nondeterminism}. The white-box
 * tests ensure that the automaton is in the expected state after computation.
 */
public class NondeterminismTest {

  /**
   * White-box test: Given the number of computation cycles, ensures that the
   * automaton transitions into the expected states.
   *
   * @param cycles the number of computation cycles to run the simulation,
   * should match the number of expected states (minus one because of the
   * initial state)
   * @param expected the states the automaton is expected to visit in order
   */
  @ParameterizedTest
  @MethodSource("expStatesProvider")
  @DisplayName("Source should visit expected states")
  public void shouldVisitExpectedStates(int cycles,
                                        @NotNull NondeterminismState[] expected) {
    Preconditions.checkNotNull(expected);
    Preconditions.checkArgument(cycles >= 0);
    Preconditions.checkArgument(expected.length >= 1);
    Preconditions.checkArgument(expected.length == cycles + 1);

    // Given
    Nondeterminism component = new Nondeterminism();
    component.setUp();
    component.init();

    // When
    List<NondeterminismState> actual = new ArrayList<>(cycles);

    // add the initial state
    actual.add(((NondeterminismImpl) component.behaviorImpl).currentState);
    for (int i = 0; i < cycles; i++) {
      component.compute();
      component.update();

      // add the current state after state transition
      actual.add(((NondeterminismImpl) component.behaviorImpl).currentState);
    }

    // Then
    Assertions.assertThat(actual).containsExactly(expected);
  }

  /**
   * @return The number of computation cycles the simulation should run and an
   * array of the states the automaton is expected to visit.
   */
  protected static Stream<Arguments> expStatesProvider() {
    return Stream.of(
        Arguments.of(0, new NondeterminismState[]{
            NondeterminismState.A }),
        Arguments.of(1, new NondeterminismState[]{
            NondeterminismState.A,
            NondeterminismState.B }),
        Arguments.of(2, new NondeterminismState[]{
            NondeterminismState.A,
            NondeterminismState.B,
            NondeterminismState.C }),
        Arguments.of(3, new NondeterminismState[]{
            NondeterminismState.A,
            NondeterminismState.B,
            NondeterminismState.C,
            NondeterminismState.A }),
        Arguments.of(4, new NondeterminismState[]{
            NondeterminismState.A,
            NondeterminismState.B,
            NondeterminismState.C,
            NondeterminismState.A,
            NondeterminismState.B })
    );
  }
}