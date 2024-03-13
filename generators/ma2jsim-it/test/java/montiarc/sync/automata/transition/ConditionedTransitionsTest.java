/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.transition;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.ITimeAwareInPort;
import montiarc.rte.scheduling.InstantSchedule;
import montiarc.types.NumberSign;
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
import static montiarc.types.NumberSign.POSITIVE;
import static montiarc.types.NumberSign.NEGATIVE;
import static montiarc.types.NumberSign.ZERO;

@ExtendWith(MockitoExtension.class)
class ConditionedTransitionsTest {

  /** capture of the actual output stream on port o */
  @Captor ArgumentCaptor<Message<NumberSign>> actual;

  /** the target port of output port o */
  @Mock ITimeAwareInPort<NumberSign> port_o;

  /**
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Integer>> input,
              @NotNull List<Message<NumberSign>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    ConditionedTransitionsComp sut = new ConditionedTransitionsCompBuilder().setScheduler(new InstantSchedule()).setName("sut").build();

    sut.port_o().connect(this.port_o);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_o).receive(this.actual.capture());

    // When
    sut.init();

    for (Message<Integer> msg : input) {
      sut.port_i().receive(msg);
    }

    // Then
    Assertions.assertThat(this.actual.getAllValues()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(msg(1), tk(), msg(-3), tk(), msg(0), tk()),
        List.of(msg(POSITIVE), tk(), msg(NEGATIVE), tk(), msg(ZERO), tk())
      )
    );
  }
}
