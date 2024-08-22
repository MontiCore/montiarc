/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.InPort;
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

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@ExtendWith(MockitoExtension.class)
class DelayTest {

  /**
   * capture of the actual output stream on port o
   */
  @Captor
  ArgumentCaptor<Message<OnOff>> actual;

  /**
   * the target port of output port o
   */
  @Mock
  InPort<OnOff> port_o;

  /**
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<OnOff>> input,
              @NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    DelayComp sut = new DelayCompBuilder().setName("sut").build();

    sut.port_o().connect(this.port_o);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_o).receive(this.actual.capture());

    // When
    sut.init();

    for (Message<OnOff> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(this.actual.getAllValues()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(),
        List.of(msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.ON)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON))
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF))
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON), tk(), msg(OnOff.ON))
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk(), msg(OnOff.OFF)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON), tk(), msg(OnOff.OFF))
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), msg(OnOff.ON))
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF))
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON), tk(), msg(OnOff.ON)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON), tk(), msg(OnOff.ON), tk(), msg(OnOff.ON))
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF))
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), tk(), msg(OnOff.OFF)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), tk(), msg(OnOff.OFF))
      ),
      Arguments.of(
        List.of(tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF)),
        List.of(msg(OnOff.OFF), tk(), tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF))
      )
    );
  }
}
