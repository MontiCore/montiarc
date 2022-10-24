/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata.Sink.States;
import com.google.common.base.Preconditions;
import montiarc.rte.timesync.DelayedPort;
import montiarc.rte.timesync.Port;
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
 * The system under test is the component {@code Sink}. The white-box tests
 * ensure that the automaton is in the expected state after computation.
 */
public class SinkTest {

  /**
   * White-box test: Given the input, ensures that the automaton transitions
   * into the expected states.
   *
   * @param input the inputs given to the component under test in order, its
   * length should match the number of expected output messages (minus one
   * because of the initial state)
   * @param expected the states the automaton is expected to visit in order
   */
  @ParameterizedTest
  @MethodSource("inputAndExpStatesProvider")
  @DisplayName("Sink should visit expected states")
  public void shouldVisitExpected(@NotNull OnOff[] input,
                                  @NotNull States[] expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);
    Preconditions.checkArgument(expected.length >= 1);
    Preconditions.checkArgument(expected.length == input.length + 1);

    // Given
    Sink sink = new Sink();
    sink.setUp(new DelayedPort<>());

    // When
    List<States> actual = new ArrayList<>(input.length);

    // add the initial state
    actual.add(sink.getCurrentState());
    for (OnOff value : input) {
      ((Port<OnOff>) sink.getI()).setValue(value);
      ((Port<OnOff>) sink.getI()).update();
      sink.compute();

      // add the current state after state transition
      actual.add(sink.getCurrentState());
    }

    // Then
    Assertions.assertThat(actual).containsExactly(expected);
  }

  /**
   * @return An array of input messages and an array of the states the automaton
   * is expected to visit for the given input.
   */
  protected static Stream<Arguments> inputAndExpStatesProvider() {
    return Stream.of(
      // No input value, visit the initial state A
      Arguments.of(new OnOff[]{}, new States[]{ States.A }),
      // i == ON, transition from A to B
      Arguments.of(new OnOff[]{ OnOff.ON }, new States[]{ States.A, States.B }),
      // i == OFF, underspecified, stay in A
      Arguments.of(new OnOff[]{ OnOff.OFF },
        new States[]{ States.A, States.A }
      ),
      // i == <ON, OFF>, transition from A to B to A
      Arguments.of(new OnOff[]{ OnOff.ON, OnOff.OFF },
        new States[]{ States.A, States.B, States.A }
      ),
      // i == <ON, ON>, underspecified, transition from A to B and stay in B
      Arguments.of(new OnOff[]{ OnOff.ON, OnOff.ON },
        new States[]{ States.A, States.B, States.B }
      )
    );
  }
}
