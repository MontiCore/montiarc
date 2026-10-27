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
class DisplayTest {

  /**
   * @param show  the input stream on port show
   * @param word  the input stream on port word
   * @param value the expected output stream on port value
   */
  @ParameterizedTest
  @MethodSource("io")
  void test(@NotNull List<Message<Boolean>> show,
            @NotNull List<Message<Integer>> word,
            @NotNull List<Message<Double>> value) {
    // Given
    DisplayComp sut = new DisplayCompBuilder().setName("sut").build();
    PortObserver<Double> port_value = new PortObserver<>();

    sut.port_value().connect(port_value);

    // When
    for (Message<Boolean> msg : show) {
      sut.port_show().receive(msg);
    }
    for (Message<Integer> msg : word) {
      sut.port_word().receive(msg);
    }

    sut.runToCompletion();

    // Then
    assertThat(port_value.getObservedMessages()).containsExactlyElementsOf(value);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // <false>, <8192> -> <0.0>
      Arguments.of(
        List.of(msg(false), tk()),
        List.of(msg(8192), tk()),
        List.of(msg(0.0), tk())
      ),
      // <true>, <8192> -> <1.5>
      Arguments.of(
        List.of(msg(true), tk()),
        List.of(msg(8192), tk()),
        List.of(msg(1.5), tk())
      ),
      // <true>, <zero> -> <0.0>
      Arguments.of(
        List.of(msg(true), tk()),
        List.of(msg(1048576), tk()),
        List.of(msg(0.0), tk())
      ),
      // <true>, <sign, exponent -1, 8192> -> <-0.75>
      Arguments.of(
        List.of(msg(true), tk()),
        List.of(msg(4186112), tk()),
        List.of(msg(-0.75), tk())
      ),
      // <true>, <exponent 10> -> <1024>
      Arguments.of(
        List.of(msg(true), tk()),
        List.of(msg(163840), tk()),
        List.of(msg(1024.0), tk())
      ),
      // <true>, <infinity> -> <infinity>
      Arguments.of(
        List.of(msg(true), tk()),
        List.of(msg(1032192), tk()),
        List.of(msg(Double.POSITIVE_INFINITY), tk())
      ),
      // <true, false>, <8192, 0> -> <1.5, 1.5>
      Arguments.of(
        List.of(msg(true), tk(), msg(false), tk()),
        List.of(msg(8192), tk(), msg(0), tk()),
        List.of(msg(1.5), tk(), msg(1.5), tk())
      )
    );
  }
}
