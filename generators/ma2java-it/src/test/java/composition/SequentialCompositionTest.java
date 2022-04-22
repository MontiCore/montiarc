/* (c) https://github.com/MontiCore/monticore */
package composition;

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
 * The system under test is the component {@code SequentialComposition}. The
 * black-box tests ensure that the system produces the expected outputs.
 */
public class SequentialCompositionTest {

  /**
   * Black-box test: Ensures that the topology of subcomponents produces the
   * expected outputs.
   *
   * @param input  the inputs given to the component under test in order
   * @param output the expected output messages in order, its length should
   *               match the number of input messages (minus two because of
   *               missing initial output)
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
    SequentialComposition component = new SequentialComposition();
    component.setUp();
    component.init();

    // provide initial input
    component.getPortI().setNextValue(input[0]);
    component.getPortI().update();
    component.update();
    component.getPortI().setNextValue(input[1]);
    component.compute();
    component.getPortI().update();
    component.update();

    // When
    List<OnOff> actual = new ArrayList<>(output.length);
    // no initial output
    for (int i = 2; i < input.length; i++) {
      component.getPortI().setNextValue(input[i]);
      component.compute();
      component.getPortI().update();
      component.update();

      // add the current value after computation
      actual.add(component.getPortO().getCurrentValue());
    }

    // Then
    Assertions.assertThat(actual).containsExactly(output);
  }

  /**
   * @return An array of input messages and an array of the expected outputs the
   * sequentially composed components should produce for the given input.
   */
  protected static Stream<Arguments> inputAndExpectedOutputProvider() {
    return Stream.of(
        Arguments.of(
            new OnOff[]{OnOff.ON, OnOff.OFF},
            new OnOff[]{}
        ),
        Arguments.of(
            new OnOff[]{OnOff.ON, OnOff.OFF},
            new OnOff[]{}
        ),
        Arguments.of(
            new OnOff[]{OnOff.ON, OnOff.OFF, OnOff.ON},
            new OnOff[]{OnOff.OFF}
        ),
        Arguments.of(
            new OnOff[]{OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF},
            new OnOff[]{OnOff.OFF, OnOff.ON}
        ),
        Arguments.of(
            new OnOff[]{OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON},
            new OnOff[]{OnOff.OFF, OnOff.ON, OnOff.OFF}
        )
    );
  }
}
