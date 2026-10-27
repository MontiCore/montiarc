/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class KeyboardTest {

  /**
   * @param value the input stream on port value
   * @param word  the expected output stream on port word
   */
  @ParameterizedTest
  @MethodSource("io")
  void test(@NotNull List<Message<Double>> value,
            @NotNull List<Message<Integer>> word) {
    // Given
    KeyboardComp sut = new KeyboardCompBuilder().setName("sut").build();
    PortObserver<Integer> port_word = new PortObserver<>();

    sut.port_word().connect(port_word);

    // When
    for (Message<Double> msg : value) {
      sut.port_value().receive(msg);
    }

    sut.runToCompletion();

    // Then
    assertThat(port_word.getObservedMessages()).containsExactlyElementsOf(word);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // <1.0> -> <0>
      Arguments.of(
        List.of(msg(1.0), tk()),
        List.of(msg(0), tk())
      ),
      // <0.0> -> <zero>
      Arguments.of(
        List.of(msg(0.0), tk()),
        List.of(msg(1048576), tk())
      ),
      // <1.5> -> <8192>
      Arguments.of(
        List.of(msg(1.5), tk()),
        List.of(msg(8192), tk())
      ),
      // <-0.75> -> <sign, exponent -1, 8192>
      Arguments.of(
        List.of(msg(-0.75), tk()),
        List.of(msg(4186112), tk())
      ),
      // <1024> -> <exponent 10>
      Arguments.of(
        List.of(msg(1024.0), tk()),
        List.of(msg(163840), tk())
      ),
      // <1e30> -> <infinity>
      Arguments.of(
        List.of(msg(1.0E30), tk()),
        List.of(msg(1032192), tk())
      ),
      // <1e-30> -> <zero>
      Arguments.of(
        List.of(msg(1.0E-30), tk()),
        List.of(msg(1048576), tk())
      ),
      // <1.0, 1.5> -> <0, 8192>
      Arguments.of(
        List.of(msg(1.0), tk(), msg(1.5), tk()),
        List.of(msg(0), tk(), msg(8192), tk())
      )
    );
  }
}
