/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.types.OnOff;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static montiarc.types.OnOff.OFF;
import static montiarc.types.OnOff.ON;


class IgnoresInPortTest {

  /**
   * @param input_i1 the input stream on port i1
   * @param input_i2 the input stream on port i2
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<OnOff>> input_i1,
              @NotNull List<Message<OnOff>> input_i2,
              @NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(input_i1);
    Preconditions.checkNotNull(input_i2);
    Preconditions.checkNotNull(expected);

    // Given
    IgnoresInPortComp sut = new IgnoresInPortCompBuilder().setName("sut").build();
    PortObserver<OnOff> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    input_i1.forEach(sut.port_i1()::receive);
    input_i2.forEach(sut.port_i2()::receive);

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // Test case: Messages on ports that do not trigger anything are dropped.
      // I.e. the messages do not remain in the buffer, blocking time progress,
      // which is only possible when ticks are at the front of each port queue.
      Arguments.of(
        /* i1 */ List.of(msg(ON), msg(ON), tk(), msg(ON), msg(ON)),
        /* i2 */ List.of(msg(OFF), tk()),
        /*  o */ List.of(msg(ON), msg(ON), tk(), msg(ON), msg(ON))
      ),
      // Testing the same, only with a different configuration:
      Arguments.of(
        /* i1 */ List.of(tk(), msg(ON)),
        /* i2 */ List.of(msg(OFF), msg(OFF), tk()),
        /*  o */ List.of(tk(), msg(ON))
      ),
      // Test case: Even though i2 does not trigger any transition, time progress
      // is only possible, if ticks are present at every input port, including i2.
      Arguments.of(
        /* i1 */ List.of(msg(ON), tk(), msg(ON), tk(), msg(ON)),
        /* i2 */ List.of(msg(OFF), tk(), msg(OFF)),
        /*  o */ List.of(msg(ON), tk(), msg(ON))
      )
    );
  }
}
