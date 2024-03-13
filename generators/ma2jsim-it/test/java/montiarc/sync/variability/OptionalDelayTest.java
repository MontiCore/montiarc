/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.variability;

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

@ExtendWith(MockitoExtension.class)
class OptionalDelayTest {

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
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("ioDelayed")
  void testIODelayed(@NotNull List<Message<OnOff>> input,
              @NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    OptionalDelayComp sut = new OptionalDelayCompBuilder().setScheduler(new InstantSchedule()).setName("sut").set_feature_delayed(true).build();

    sut.port_o0().connect(this.port_o);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_o).receive(this.actual.capture());

    // When
    sut.init();

    for (Message<OnOff> msg : input) {
      sut.port_i().receive(msg);
    }

    // Then
    Assertions.assertThat(this.actual.getAllValues()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> ioDelayed() {
    return Stream.of(
      Arguments.of(
        List.of(),
        List.of(msg(OnOff.OFF), tk())
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
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON), tk(), msg(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk(), msg(OnOff.OFF), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON), tk(), msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), msg(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON), tk(), msg(OnOff.ON), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON), tk(), msg(OnOff.ON), tk(), msg(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), tk(), msg(OnOff.OFF), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), tk(), msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk()),
        List.of(msg(OnOff.OFF), tk(), tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk())
      )
    );
  }

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
    OptionalDelayComp sut = new OptionalDelayCompBuilder().setScheduler(new InstantSchedule()).setName("sut").set_feature_delayed(false).build();

    sut.port_o1().connect(this.port_o);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_o).receive(this.actual.capture());

    // When
    sut.init();

    for (Message<OnOff> msg : input) {
      sut.port_i().receive(msg);
    }

    // Then
    Assertions.assertThat(this.actual.getAllValues()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(msg(OnOff.ON), tk()),
        List.of(msg(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk()),
        List.of(msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON), tk()),
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk(), msg(OnOff.OFF), tk()),
        List.of(msg(OnOff.ON), tk(), msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON), tk(), msg(OnOff.ON), tk()),
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON), tk(), msg(OnOff.ON), tk())
      ));
  }
}
