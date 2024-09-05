/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

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
class HierarchyTest {

  /**
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Integer>> input,
              @NotNull List<Message<String>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    HierarchyComp sut = new HierarchyCompBuilder().setName("sut").build();
    PortObserver<String> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<Integer> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(msg(1), tk()), // input
        List.of(msg("first"), tk()) // expected
      ),
      Arguments.of(
        List.of(tk()), // input
        List.of(tk()) // expected
      ),
      Arguments.of(
        List.of(msg(2), tk()), // input
        List.of(msg("second"), tk()) // expected
      ),
      Arguments.of(
        List.of(msg(3), tk()), // input
        List.of(msg("third"), tk()) // expected
      ),
      Arguments.of(
        List.of(msg(4), tk()), // input
        List.of(msg("fourth"), tk()) // expected
      ),
      Arguments.of(
        List.of(msg(5), tk()), // input
        List.of(msg("fifth"), tk()) // expected
      ),
      Arguments.of(
        List.of(msg(2), tk(), msg(3), tk()), // input
        List.of(msg("second"), tk(), msg("third"), tk()) // expected
      ),
      Arguments.of(
        List.of(msg(1), tk(), msg(6), tk()), // input
        List.of(msg("first"), tk(), msg("sixth"), tk()) // expected
      )
    );
  }
}