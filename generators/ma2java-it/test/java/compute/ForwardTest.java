/* (c) https://github.com/MontiCore/monticore */
package compute;

import com.google.common.base.Preconditions;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import Types.OnOff;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ForwardTest {

  @ParameterizedTest
  @MethodSource("inputAndExpectedOutputProvider")
  public void shouldProduceExpectedOutput(@NotNull OnOff[] input,
                                          @NotNull OnOff[] output) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(output);
    Preconditions.checkArgument(input.length >= 1);
    Preconditions.checkArgument(input.length == output.length);

    // Given
    Forward forward = new Forward();
    forward.setUp();
    forward.init();

    // When
    List<OnOff> actual = new ArrayList<>(output.length);

    for (OnOff onOff : input) {
      // compute
      forward.getI().update(onOff);
      forward.compute();

      // get output
      actual.add(forward.getO().getValue());

      // tick
      forward.getI().tick();
      forward.tick();
    }

    // Then
    Assertions.assertThat(actual.size()).isEqualTo(output.length);
    Assertions.assertThat(actual).containsExactly(output);
  }

  static Stream<Arguments> inputAndExpectedOutputProvider() {
    return Stream.of(
      Arguments.of(new OnOff[]{ OnOff.ON },
        new OnOff[]{ OnOff.ON }),
      Arguments.of(new OnOff[]{ OnOff.OFF },
        new OnOff[]{ OnOff.OFF }),
      Arguments.of(new OnOff[]{ OnOff.ON, OnOff.OFF },
        new OnOff[]{ OnOff.ON, OnOff.OFF }),
      Arguments.of(new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON },
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON }),
      Arguments.of(new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF },
        new OnOff[]{ OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF })
    );
  }
}
