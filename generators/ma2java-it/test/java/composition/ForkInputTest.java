/* (c) https://github.com/MontiCore/monticore */
package composition;

import Types.OnOff;
import com.google.common.base.Preconditions;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ForkInputTest {

  @ParameterizedTest
  @MethodSource("inputAndExpectedOutputProvider")
  public void shouldProduceExpectedOutput(@NotNull OnOff[] input,
                                          @NotNull OnOff[] output1,
                                          @NotNull OnOff[] output2) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(output1);
    Preconditions.checkNotNull(output2);
    Preconditions.checkArgument(input.length >= 1);
    Preconditions.checkArgument(input.length == output1.length);
    Preconditions.checkArgument(input.length == output2.length);

    //Given
    ForkInput component = new ForkInput();
    component.setUp();
    component.init();

    // When
    List<OnOff> actual1 = new ArrayList<>(output1.length);
    List<OnOff> actual2 = new ArrayList<>(output2.length);
    for (OnOff onOff : input) {
      // compute
      component.getI().update(onOff);
      component.compute();

      // get output
      actual1.add(component.getO1().getValue());
      actual2.add(component.getO2().getValue());

      // tick
      component.getI().tick();
      component.tick();
    }

    // Then
    Assertions.assertThat(actual1).containsExactly(output1);
    Assertions.assertThat(actual2).containsExactly(output2);
  }

  protected static Stream<Arguments> inputAndExpectedOutputProvider() {
    return Stream.of(
      // 1
      Arguments.of(
        new OnOff[]{ OnOff.ON},
        new OnOff[]{ OnOff.ON},
        new OnOff[]{ OnOff.ON}
      ),
      // 2
      Arguments.of(
        new OnOff[]{ OnOff.OFF},
        new OnOff[]{ OnOff.OFF},
        new OnOff[]{ OnOff.OFF}
      ),
      // 3
      Arguments.of(
        new OnOff[]{ OnOff.ON, OnOff.ON},
        new OnOff[]{ OnOff.ON, OnOff.ON},
        new OnOff[]{ OnOff.ON, OnOff.ON}
      ),
      // 4
      Arguments.of(
        new OnOff[]{ OnOff.ON, OnOff.OFF},
        new OnOff[]{ OnOff.ON, OnOff.OFF},
        new OnOff[]{ OnOff.ON, OnOff.OFF}
      ),
      // 5
      Arguments.of(
        new OnOff[]{ OnOff.OFF, OnOff.ON},
        new OnOff[]{ OnOff.OFF, OnOff.ON},
        new OnOff[]{ OnOff.OFF, OnOff.ON}
      ),
      // 6
      Arguments.of(
        new OnOff[]{ OnOff.OFF, OnOff.OFF},
        new OnOff[]{ OnOff.OFF, OnOff.OFF},
        new OnOff[]{ OnOff.OFF, OnOff.OFF}
      )
    );
  }
}
