/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.types.OnOff;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

class SinkTest {

  /**
   * @param input the input stream on port i
   * @param expected the expected states visited
   */
  @ParameterizedTest
  @MethodSource("io")
  void testST(@NotNull List<Message<OnOff>> input,
              @NotNull List<String> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    SinkComp sut = new SinkCompBuilder().setName("sut").build();

    // When
    sut.init();

    List<String> actual = new ArrayList<>(expected.size());

    // When
    sut.init();

    for (Message<OnOff> msg : input) {
      sut.port_i().receive(msg);
      sut.port_i().receive(tk());
      sut.run(1);

      actual.add(((SinkAutomaton) sut.getBehavior()).getState().name());
    }

    // Then
    Assertions.assertThat(actual).containsExactlyElementsOf(expected);

    // Then
    Assertions.assertThat(actual).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // 1
      Arguments.of(
        List.of(msg(OnOff.ON)),
        List.of("On")
      ),
      // 2
      Arguments.of(
        List.of(msg(OnOff.OFF)),
        List.of("Off")
      ),
      // 3
      Arguments.of(
        List.of(msg(OnOff.ON), msg(OnOff.ON)),
        List.of("On", "On")
      ),
      // 4
      Arguments.of(
        List.of(msg(OnOff.ON), msg(OnOff.OFF)),
        List.of("On", "Off")
      ),
      // 5
      Arguments.of(
        List.of(msg(OnOff.OFF), msg(OnOff.ON)),
        List.of("Off", "On")
      ),
      // 6
      Arguments.of(
        List.of(msg(OnOff.OFF), msg(OnOff.OFF)),
        List.of("Off", "Off")
      ),
      // 7
      Arguments.of(
        List.of(msg(OnOff.ON), msg(OnOff.ON), msg(OnOff.ON)),
        List.of("On", "On", "On")
      ));
  }
}
