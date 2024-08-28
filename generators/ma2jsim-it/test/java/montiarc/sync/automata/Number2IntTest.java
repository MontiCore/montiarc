/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.tk;

class Number2IntTest {

  /**
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Number>> input,
              @NotNull List<Message<Number>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    Number2IntComp<Number> sut = new Number2IntCompBuilder<>().setName("sut").build();
    PortObserver<Number> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<Number> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(new Message<>(0), tk()),
        List.of(new Message<>(0), tk())
      ),
      Arguments.of(
        List.of(new Message<>(1.5), tk()),
        List.of(new Message<>(1), tk())
      ),
      Arguments.of(
        List.of(new Message<>(1.5), tk(), new Message<>(-1), tk()),
        List.of(new Message<>(1), tk(), new Message<>(-1), tk())
      ),
      Arguments.of(
        List.of(new Message<>(1F), tk(), new Message<>(3.333333), tk(), new Message<>(-1.6F), tk()),
        List.of(new Message<>(1), tk(), new Message<>(3), tk(), new Message<>(-1), tk())
      ));
  }
}
