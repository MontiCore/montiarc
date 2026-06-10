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
class SwitchStringTest {

  /**
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<String>> input,
              @NotNull List<Message<Integer>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    SwitchStringComp sut = new SwitchStringCompBuilder().setName("sut").build();
    PortObserver<Integer> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    for (Message<String> m : input) {
      sut.port_i().receive(m);
    }

    sut.runToCompletion();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(msg("hello"), tk()),
        List.of(msg(1), tk())
      ),
      Arguments.of(
        List.of(msg("world"), tk()),
        List.of(msg(2), tk())
      ),
      Arguments.of(
        List.of(msg("test"), tk()),
        List.of(msg(3), tk())
      ),
      Arguments.of(
        List.of(msg("unknown"), tk()),
        List.of(msg(0), tk())
      ),
      Arguments.of(
        List.of(msg(""), tk()),
        List.of(msg(0), tk())
      ),
      Arguments.of(
        List.of(msg("hello"), tk(), msg("world"), tk(), msg("other"), tk()),
        List.of(msg(1), tk(), msg(2), tk(), msg(0), tk())
      )
    );
  }
}
