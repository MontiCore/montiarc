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
 * The system under test is the component {@code ParallelComposition}. The
 * black-box tests ensure that the system produces the expected outputs.
 */
public class ParallelCompositionTest {

  /**
   * Black-box test: Ensures that the topology of subcomponents produces the
   * expected outputs.
   *
   * @param input1 the inputs given to the first input port of the component
   * under test in order
   * @param input2 the inputs given to the second input port of the component
   * under test in order
   * @param output1 the expected output messages on the first output port in
   * order, its length should match the number of input messages (minus two
   * because of missing initial output)
   * @param output2 the expected output messages on the second output port in
   * order, its length should match the number of input messages (minus two
   * because of missing initial output)
   */
  @ParameterizedTest
  @MethodSource("inputAndExpectedOutputProvider")
  @DisplayName("Component with subcomponent should produce expected outputs")
  public void shouldProduceExpectedOutput(@NotNull OnOff[] input1,
                                          @NotNull OnOff[] input2,
                                          @NotNull OnOff[] output1,
                                          @NotNull OnOff[] output2) {
    Preconditions.checkNotNull(input1);
    Preconditions.checkNotNull(input2);
    Preconditions.checkNotNull(output1);
    Preconditions.checkNotNull(output2);
    Preconditions.checkArgument(input1.length >= 1);
    Preconditions.checkArgument(input2.length >= 1);
    Preconditions.checkArgument(input1.length == input2.length);
    Preconditions.checkArgument(input1.length == output1.length);
    Preconditions.checkArgument(input2.length == output2.length);

    //Given
    ParallelComposition component = new ParallelComposition();
    component.setUp();
    component.init();

    // When
    List<OnOff> actual1 = new ArrayList<>(output1.length);
    List<OnOff> actual2 = new ArrayList<>(output2.length);
    for (int i = 0; i < input1.length; i++) {
      // compute
      component.getI1().update(input1[i]);
      component.getI2().update(input2[i]);
      component.compute();

      // get output
      actual1.add(component.getO1().getValue());
      actual2.add(component.getO2().getValue());

      // tick
      component.getI1().tick();
      component.getI2().tick();
      component.tick();
    }

    // Then
    Assertions.assertThat(actual1).containsExactly(output1);
    Assertions.assertThat(actual2).containsExactly(output2);
  }

  /**
   * @return An array of input messages and an array of the expected outputs the
   * sequentially composed components should produce for the given input.
   */
  protected static Stream<Arguments> inputAndExpectedOutputProvider() {
    return Stream.of(
      // 1
      Arguments.of(
        new OnOff[]{ OnOff.ON },
        new OnOff[]{ OnOff.ON },
        new OnOff[]{ null },
        new OnOff[]{ OnOff.OFF }
      ),
      // 2
      Arguments.of(
        new OnOff[]{ OnOff.ON, OnOff.OFF },
        new OnOff[]{ OnOff.ON, OnOff.OFF },
        new OnOff[]{ null, OnOff.ON },
        new OnOff[]{ OnOff.OFF, OnOff.ON }
      ),
      // 3
      Arguments.of(
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON },
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON },
        new OnOff[]{ null, OnOff.ON, OnOff.OFF },
        new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF }
      ),
      // 4
      Arguments.of(
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF },
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF },
        new OnOff[]{ null, OnOff.ON, OnOff.OFF, OnOff.ON },
        new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON }
      ),
      // 5
      Arguments.of(
        new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON },
        new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON },
        new OnOff[]{ null, OnOff.OFF, OnOff.ON, OnOff.OFF },
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF }
      ),
      // 6
      Arguments.of(
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF },
        new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON },
        new OnOff[]{ null, OnOff.ON, OnOff.OFF, OnOff.ON },
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF }
      ),
      // 7
      Arguments.of(
        new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON },
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF },
        new OnOff[]{ null, OnOff.OFF, OnOff.ON, OnOff.OFF },
        new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON }
      )
    );
  }
}
