/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.ITimeAwareInPort;
import montiarc.rte.scheduling.InstantSchedule;
import montiarc.types.OnOff;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.MsgFactory.msg;
import static montiarc.MsgFactory.tk;
import static montiarc.types.OnOff.ON;
import static montiarc.types.OnOff.OFF;


@ExtendWith(MockitoExtension.class)
class IgnoresInPortTest {

  /**
   * capture of the actual output stream on port o
   */
  @Captor
  ArgumentCaptor<Message<OnOff>> actual;

  /**
   * the target port of output port o
   */
  @Mock
  ITimeAwareInPort<OnOff> port_o;

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
    IgnoresInPortComp sut = new IgnoresInPortCompBuilder().setScheduler(new InstantSchedule()).setName("sut").build();

    sut.port_o().connect(this.port_o);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_o).receive(this.actual.capture());

    // When
    sut.init();

    input_i1.forEach(sut.port_i1()::receive);
    input_i2.forEach(sut.port_i2()::receive);

    // Then
    Assertions.assertThat(this.actual.getAllValues()).containsExactlyElementsOf(expected);
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
