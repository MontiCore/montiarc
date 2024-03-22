/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.ITimeAwareInPort;
import montiarc.rte.scheduling.InstantSchedule;
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

@ExtendWith(MockitoExtension.class)
class ParamsBool1Test {

  /**
   * capture of the actual output stream on port o1
   */
  @Captor
  ArgumentCaptor<Message<Boolean>> actual_o1;

  /**
   * the target port of output port o1
   */
  @Mock
  ITimeAwareInPort<Boolean> port_o1;

  /**
   * capture of the actual output stream on port o2
   */
  @Captor
  ArgumentCaptor<Message<Boolean>> actual_o2;

  /**
   * the target port of output port o2
   */
  @Mock
  ITimeAwareInPort<Boolean> port_o2;

  /**
   * @param p1 the argument for parameter p1
   * @param p2 the argument for parameter p2
   * @param input the input stream on port i
   * @param expected_o1 the expected output stream on port o1
   * @param expected_o2 the expected output stream on port o2
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(boolean p1, boolean p2,
              @NotNull List<Message<Boolean>> input,
              @NotNull List<Message<Boolean>> expected_o1,
              @NotNull List<Message<Boolean>> expected_o2) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected_o1);
    Preconditions.checkNotNull(expected_o2);

    // Given
    ParamsBool1Comp sut = new ParamsBool1CompBuilder()
      .setScheduler(new InstantSchedule())
      .set_param_p1(p1)
      .set_param_p2(p2)
      .setName("sut").build();

    sut.port_o1().connect(this.port_o1);
    sut.port_o2().connect(this.port_o2);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_o1).receive(this.actual_o1.capture());
    Mockito.doNothing().when(this.port_o2).receive(this.actual_o2.capture());

    // When
    sut.init();

    for (Message<Boolean> msg : input) {
      sut.port_i().receive(msg);
    }

    // Then
    Assertions.assertThat(this.actual_o1.getAllValues()).containsExactlyElementsOf(expected_o1);
    Assertions.assertThat(this.actual_o2.getAllValues()).containsExactlyElementsOf(expected_o2);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // 1
      Arguments.of(
        true, true,
        List.of(msg(true)),
        List.of(msg(true)),
        List.of(msg(true))
      ),
      // 2
      Arguments.of(
        true, true,
        List.of(msg(false)),
        List.of(msg(true)),
        List.of(msg(true))
      ),
      // 3
      Arguments.of(
        true, false,
        List.of(msg(true)),
        List.of(msg(true)),
        List.of(msg(false))
      ),
      // 4
      Arguments.of(
        true, false,
        List.of(msg(false)),
        List.of(msg(true)),
        List.of(msg(false))
      ),
      // 5
      Arguments.of(
        false, true,
        List.of(msg(true)),
        List.of(msg(false)),
        List.of(msg(true))
      ),
      // 6
      Arguments.of(
        false, true,
        List.of(msg(false)),
        List.of(msg(false)),
        List.of(msg(true))
      ),
      // 7
      Arguments.of(
        false, false,
        List.of(msg(true)),
        List.of(msg(false)),
        List.of(msg(false))
      ),
      // 8
      Arguments.of(
        false, false,
        List.of(msg(false)),
        List.of(msg(false)),
        List.of(msg(false))
      ),
      // 9
      Arguments.of(
        true, true,
        List.of(msg(true), msg(true)),
        List.of(msg(true), msg(true)),
        List.of(msg(true), msg(true))
      ),
      // 10
      Arguments.of(
        true, false,
        List.of(msg(true), msg(true)),
        List.of(msg(true), msg(true)),
        List.of(msg(false), msg(false))
      ),
      // 11
      Arguments.of(
        false, true,
        List.of(msg(true), msg(true)),
        List.of(msg(false), msg(false)),
        List.of(msg(true), msg(true))
      ),
      // 12
      Arguments.of(
        false, false,
        List.of(msg(true), msg(true)),
        List.of(msg(false), msg(false)),
        List.of(msg(false), msg(false))
      )
    );
  }
}
