/* (c) https://github.com/MontiCore/monticore */
package composition;


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
 * The system under test is the component {@code FeedbackLoop}. The black-box
 * tests ensure that the system produces the expected outputs.
 */
public class FeedbackLoopTest {

  /**
   * Black-box test: Ensures that the topology of subcomponents produces the
   * expected outputs.
   *
   * @param input the inputs given to the component under test in order
   * @param output the expected output messages in order, its length should
   * match the number of input messages (minus two because of missing initial
   * output)
   */
  @ParameterizedTest
  @MethodSource("inputAndExpectedOutputProvider")
  @DisplayName("Component with subcomponent should produce expected outputs")
  public void shouldProduceExpectedOutput(@NotNull OnOff[] input,
                                          @NotNull OnOff[] output) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(output);
    Preconditions.checkArgument(input.length >= 2);
    Preconditions.checkArgument(input.length == output.length + 2);

    //Given
    FeedbackLoop component = new FeedbackLoop();
    component.setUp(new DelayedPort<>(), new DelayedPort<>());

    // provide initial input
    ((Port<OnOff>) component.getI()).setValue(input[0]);
    ((Port<OnOff>) component.getI()).update();
    component.update();
    ((Port<OnOff>) component.getI()).setValue(input[1]);
    component.compute();
    ((Port<OnOff>) component.getI()).update();
    component.update();

    // When
    List<OnOff> actual = new ArrayList<>(output.length);
    // no initial output
    for (int i = 2; i < input.length; i++) {
      ((Port<OnOff>) component.getI()).setValue(input[i]);
      component.compute();
      ((Port<OnOff>) component.getI()).update();
      component.update();

      // add the current value after computation
      actual.add(component.getO().getValue());
    }

    // Then
    Assertions.assertThat(actual).containsExactly(output);
  }

  /**
   * @return An array of input messages and an array of the expected outputs the
   * composed component with feedback loop should produce for the given input.
   */
  protected static Stream<Arguments> inputAndExpectedOutputProvider() {
    return Stream.of(
      // 1
      Arguments.of(
        new OnOff[]{ OnOff.ON, OnOff.OFF },
        new OnOff[]{}
      ),
      // 2
      Arguments.of(
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON },
        new OnOff[]{ OnOff.OFF }
      ),
      // 3
      Arguments.of(
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF },
        new OnOff[]{ OnOff.OFF, OnOff.ON }
      ),
      // 4
      Arguments.of(
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON },
        new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF }
      )
    );
  }
}
