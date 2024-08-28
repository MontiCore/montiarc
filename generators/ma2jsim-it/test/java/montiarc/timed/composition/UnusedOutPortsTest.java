/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.port.ScheduledPort;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.tk;

class UnusedOutPortsTest {

  /**
   * @param i1 the input stream on port i1
   * @param i2 the input stream on port i2
   * @param o1 the expected output stream on port o1
   * @param o2 the expected output stream on port o2
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Boolean>> i1,
              @NotNull List<Message<Boolean>> i2,
              @NotNull List<Message<Boolean>> o1,
              @NotNull List<Message<Boolean>> o2) {
    Preconditions.checkNotNull(i1);
    Preconditions.checkNotNull(i2);
    Preconditions.checkNotNull(o1);
    Preconditions.checkNotNull(o2);

    // Given
    UnusedOutPortsComp sut = new UnusedOutPortsCompBuilder().setName("sut").build();
    PortObserver<Boolean> port_o1 = new PortObserver<>();
    PortObserver<Boolean> port_o2 = new PortObserver<>();

    sut.port_o1().connect(port_o1);
    sut.port_o2().connect(port_o2);

    // When
    sut.init();

    for (Message<Boolean> msg : i1) {
      sut.port_i1().receive(msg);
    }

    for (Message<Boolean> msg : i2) {
      sut.port_i2().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(((ScheduledPort<Boolean>) sut.port_i1()).getBuffer()).isEmpty();
    Assertions.assertThat(((ScheduledPort<Boolean>) sut.port_i2()).getBuffer()).isEmpty();
    Assertions.assertThat(port_o1.getObservedMessages()).containsExactlyElementsOf(o1);
    Assertions.assertThat(port_o2.getObservedMessages()).containsExactlyElementsOf(o2);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(),
        List.of(),
        List.of(),
        List.of()
      ),
      Arguments.of(
        List.of(tk()),
        List.of(tk()),
        List.of(tk()),
        List.of(tk())
      ),
      Arguments.of(
        List.of(tk(), tk()),
        List.of(tk(), tk()),
        List.of(tk(), tk()),
        List.of(tk(), tk())
      ),
      Arguments.of(
        List.of(tk(), tk(), tk()),
        List.of(tk(), tk(), tk()),
        List.of(tk(), tk(), tk()),
        List.of(tk(), tk(), tk())
      )
    );
  }
}
