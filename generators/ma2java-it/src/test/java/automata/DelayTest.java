/* (c) https://github.com/MontiCore/monticore */
package automata;

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
 * The system under test is the component {@code Delay}. The black-box tests
 * ensure that the system produces the expected outputs.
 */
public class DelayTest {

  /**
   * Black-box test: Ensures that the automaton produces the expected output.
   *
   * @param input the inputs given to the component under test in order, its
   * length should match the number of expected output messages (plus two
   * because of delay)
   * @param output the expected output messages in order
   */
  @ParameterizedTest
  @MethodSource("inputAndExpectedOutputProvider")
  @DisplayName("Component with delay should produce expected outputs")
  public void shouldProduceExpectedOutput(@NotNull OnOff[] input,
                                          @NotNull OnOff[] output) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(output);
    Preconditions.checkArgument(input.length > 0);
    Preconditions.checkArgument(input.length == output.length + 1);

    //Given
    Delay delay = new Delay();
    delay.setUp();
    delay.init();

    // provide initial input
    delay.getPortI().setNextValue(input[0]);
    delay.getPortI().update();
    delay.update();

    // When
    List<OnOff> actual = new ArrayList<>(output.length);
    // no initial output
    OnOff initial = delay.getPortO().getCurrentValue();
    for (int i = 1; i < input.length; i++) {
      delay.getPortI().setNextValue(input[i]);
      delay.compute();
      delay.getPortI().update();
      delay.update();

      // add the current value after computation
      actual.add(delay.getPortO().getCurrentValue());
    }

    // Then
    Assertions.assertThat(initial).isNull();
    Assertions.assertThat(actual).containsExactly(output);
  }

  /**
   * @return An array of input messages and an array of the expected outputs the
   * automaton should produce for the given input.
   */
  protected static Stream<Arguments> inputAndExpectedOutputProvider() {
    return Stream.of(
        Arguments.of(new OnOff[]{ OnOff.ON }, new OnOff[]{}),
        Arguments.of(new OnOff[]{ OnOff.ON, OnOff.OFF },
                     new OnOff[]{ OnOff.ON }),
        Arguments.of(new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON },
                     new OnOff[]{ OnOff.ON, OnOff.OFF }),
        Arguments.of(new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF },
                     new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON })
    );
  }
}