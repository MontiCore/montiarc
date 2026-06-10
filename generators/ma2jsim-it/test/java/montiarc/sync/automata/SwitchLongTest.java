/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class SwitchLongTest {

  /**
   * @param input    the input stream on port i (long)
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Long>> input,
              @NotNull List<Message<Integer>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    SwitchLongComp sut = new SwitchLongCompBuilder().setName("sut").build();
    PortObserver<Integer> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    for (Message<Long> m : input) {
      sut.port_i().receive(m);
    }

    sut.runToCompletion();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(msg(0L), tk()),
        List.of(msg(10), tk())
      ),
      Arguments.of(
        List.of(msg(1L), tk()),
        List.of(msg(20), tk())
      ),
      Arguments.of(
        List.of(msg(2L), tk()),
        List.of(msg(-1), tk())
      ),
      Arguments.of(
        List.of(msg(100L), tk()),
        List.of(msg(-1), tk())
      ),
      Arguments.of(
        List.of(msg(0L), tk(), msg(1L), tk(), msg(7L), tk()),
        List.of(msg(10), tk(), msg(20), tk(), msg(-1), tk())
      )
    );
  }
}
