/* (c) https://github.com/MontiCore/monticore */
package composition;

import com.google.common.base.Preconditions;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import Types.OnOff;

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
    Preconditions.checkArgument(input.length >= 1);
    Preconditions.checkArgument(input.length == output.length);

    //Given
    SequentialComposition component = new SequentialComposition();
    component.setUp();
    component.init();

    // When
    List<OnOff> actual = new ArrayList<>(output.length);
    for (OnOff onOff : input) {
      // compute
      component.getI().update(onOff);
      component.compute();

      // get output
      actual.add(component.getO().getValue());

      // tick
      component.getI().tick();
      component.tick();
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
      // 1
      Arguments.of(
        new OnOff[]{ OnOff.ON, OnOff.OFF },
        new OnOff[]{ OnOff.ON, OnOff.OFF}
      ),
      // 2
      Arguments.of(
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON },
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON }
      ),
      // 3
      Arguments.of(
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF },
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF }
      ),
      // 4
      Arguments.of(
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON },
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON }
      )
    );
  }
}
