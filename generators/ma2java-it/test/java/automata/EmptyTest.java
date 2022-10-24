/* (c) https://github.com/MontiCore/monticore */
package automata;

import com.google.common.base.Preconditions;
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
 * The system under test is the component {@code Empty}. The black-box tests
 * ensure that the system produces the expected outputs.
 */
public class EmptyTest {

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
    Preconditions.checkArgument(input.length >= 1);
    Preconditions.checkArgument(input.length == output.length);

    // Given
    Empty empty = new Empty();
    empty.setUp();

    // When
    List<OnOff> actual = new ArrayList<>(output.length);

    // no initial output
    for (OnOff onOff : input) {
      // update
      ((Port<OnOff>) empty.getI()).update();
      empty.update();

      // compute
      ((Port<OnOff>) empty.getI()).setValue(onOff);
      empty.compute();

      // get current output
      actual.add(empty.getO().getValue());
    }

    // Then
    Assertions.assertThat(actual.size()).isEqualTo(output.length);
    Assertions.assertThat(actual.get(0)).isNull();
    Assertions.assertThat(actual).containsExactly(output);
  }

  /**
   * @return An array of input messages and an array of the expected outputs the
   * automaton should produce for the given input.
   */
  protected static Stream<Arguments> inputAndExpectedOutputProvider() {
    return Stream.of(
      Arguments.of(new OnOff[]{ OnOff.ON },
        new OnOff[]{ null }),
      Arguments.of(new OnOff[]{ OnOff.ON, OnOff.OFF },
        new OnOff[]{ null, null }),
      Arguments.of(new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON },
        new OnOff[]{ null, null, null }),
      Arguments.of(new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF },
        new OnOff[]{ null, null, null, null })
    );
  }
}