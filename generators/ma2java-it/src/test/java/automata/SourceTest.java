/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata.SourceImpl.SourceState;
import com.google.common.base.Preconditions;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import types.OnOff;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The system under test is the component {@code Source}. The black-box tests
 * ensure that the system produces outputs in the expected order. The white-box
 * tests ensure that the automaton is in the expected state after computation.
 */
public class SourceTest {

  /**
   * Black-box test: Ensures that the automaton produces the expected output.
   *
   * @param cycles the number of computation cycles to run the simulation,
   * should match the number of expected output messages (no initial action)
   * @param expected the expected output messages in order
   */
  @ParameterizedTest
  @MethodSource("expOutputProvider")
  @DisplayName("Source should produce expected outputs")
  public void shouldProduceExpectedOutput(int cycles,
                                          @NotNull OnOff[] expected) {
    Preconditions.checkNotNull(expected);
    Preconditions.checkArgument(cycles > 0);
    Preconditions.checkArgument(expected.length > 0);
    Preconditions.checkArgument(expected.length == cycles);

    //Given
    Source source = new Source();
    source.setUp();
    source.init();

    // When
    List<OnOff> actual = new ArrayList<>(cycles);
    for (int i = 0; i < cycles; i++) {
      source.compute();
      source.update();

      // add the current value after computation
      actual.add(source.getPortO().getCurrentValue());
    }

    // Then
    Assertions.assertThat(actual).containsExactly(expected);
  }

  /**
   * @return The number of computation cycles the simulation should run and an
   * array of the expected outputs.
   */
  protected static Stream<Arguments> expOutputProvider() {
    return Stream.of(
        Arguments.of(1, new OnOff[]{ OnOff.ON }),
        Arguments.of(2, new OnOff[]{ OnOff.ON, OnOff.OFF }),
        Arguments.of(3, new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON }),
        Arguments.of(4, new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF })
    );
  }

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
                                        @NotNull SourceState[] expected) {
    Preconditions.checkNotNull(expected);
    Preconditions.checkArgument(cycles >= 0);
    Preconditions.checkArgument(expected.length > 0);
    Preconditions.checkArgument(expected.length == cycles + 1);

    // Given
    Source source = new Source();
    source.setUp();
    source.init();

    // When
    List<SourceState> actual = new ArrayList<>(cycles);

    // add the initial state
    actual.add(((SourceImpl) source.behaviorImpl).currentState);
    for (int i = 0; i < cycles; i++) {
      source.compute();
      source.update();

      // add the current state after state transition
      actual.add(((SourceImpl) source.behaviorImpl).currentState);
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
        Arguments.of(0, new SourceState[]{ SourceState.A }),
        Arguments.of(1, new SourceState[]{ SourceState.A, SourceState.B }),
        Arguments.of(2, new SourceState[]{ SourceState.A, SourceState.B,
            SourceState.A }),
        Arguments.of(3, new SourceState[]{ SourceState.A, SourceState.B,
            SourceState.A, SourceState.B }),
        Arguments.of(4, new SourceState[]{ SourceState.A, SourceState.B,
            SourceState.A, SourceState.B, SourceState.A })
    );
  }
}
