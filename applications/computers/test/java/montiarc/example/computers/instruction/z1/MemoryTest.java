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
class MemoryTest {

  static final int ZERO = 1048576;

  /**
   * @param address the input stream on port address
   * @param write   the input stream on port write
   * @param data    the input stream on port data
   * @param word    the expected output stream on port word
   */
  @ParameterizedTest
  @MethodSource("io")
  void test(@NotNull List<Message<Integer>> address,
            @NotNull List<Message<Boolean>> write,
            @NotNull List<Message<Integer>> data,
            @NotNull List<Message<Integer>> word) {
    // Given
    MemoryComp sut = new MemoryCompBuilder().setName("sut").build();
    PortObserver<Integer> port_word = new PortObserver<>();

    sut.port_word().connect(port_word);

    // When
    for (Message<Integer> msg : address) {
      sut.port_address().receive(msg);
    }
    for (Message<Boolean> msg : write) {
      sut.port_write().receive(msg);
    }
    for (Message<Integer> msg : data) {
      sut.port_data().receive(msg);
    }

    sut.runToCompletion();

    // Then
    assertThat(port_word.getObservedMessages()).containsExactlyElementsOf(word);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // read 0 -> zero
      Arguments.of(
        List.of(msg(0), tk()),
        List.of(msg(false), tk()),
        List.of(msg(42), tk()),
        List.of(msg(ZERO), tk())
      ),
      // read 63 -> zero
      Arguments.of(
        List.of(msg(63), tk()),
        List.of(msg(false), tk()),
        List.of(msg(42), tk()),
        List.of(msg(ZERO), tk())
      ),
      // write 42 to 5 -> 42
      Arguments.of(
        List.of(msg(5), tk()),
        List.of(msg(true), tk()),
        List.of(msg(42), tk()),
        List.of(msg(42), tk())
      ),
      // write 42 to 5, read 6, read 5 -> 42, zero, 42
      Arguments.of(
        List.of(msg(5), tk(), msg(6), tk(), msg(5), tk()),
        List.of(msg(true), tk(), msg(false), tk(), msg(false), tk()),
        List.of(msg(42), tk(), msg(7), tk(), msg(7), tk()),
        List.of(msg(42), tk(), msg(ZERO), tk(), msg(42), tk())
      )
    );
  }
}
