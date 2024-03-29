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
class ParamBool3Test {

  /**
   * capture of the actual output stream on port o
   */
  @Captor
  ArgumentCaptor<Message<Boolean>> actual;

  /**
   * the target port of output port o
   */
  @Mock
  ITimeAwareInPort<Boolean> port_o;

  /**
   * @param p the argument for parameter p
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(boolean p, @NotNull List<Message<Boolean>> input,
              @NotNull List<Message<Boolean>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    ParamBool3Comp sut = new ParamBool3CompBuilder()
      .setScheduler(new InstantSchedule())
      .set_param_p(p)
      .setName("sut").build();

    sut.port_o().connect(this.port_o);

    // when receiving a message, capture that message but do nothing else
    Mockito.lenient().doNothing().when(this.port_o).receive(this.actual.capture());

    // When
    sut.init();

    for (Message<Boolean> msg : input) {
      sut.port_i().receive(msg);
    }

    // Then
    Assertions.assertThat(this.actual.getAllValues()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // 1
      Arguments.of(
        true,
        List.of(msg(true)),
        List.of(msg(true))
      ),
      // 2
      Arguments.of(
        true,
        List.of(msg(false)),
        List.of(msg(false))
      ),
      // 3
      Arguments.of(
        false,
        List.of(msg(true)),
        List.of()
      ),
      // 4
      Arguments.of(
        false,
        List.of(msg(false)),
        List.of()
      ),
      // 5
      Arguments.of(
        true,
        List.of(msg(true), msg(true)),
        List.of(msg(true), msg(true))
      ),
      // 6
      Arguments.of(
        true,
        List.of(msg(true), msg(false)),
        List.of(msg(true), msg(false))
      ),
      // 7
      Arguments.of(
        true,
        List.of(msg(false), msg(true)),
        List.of(msg(false), msg(true))
      ),
      // 8
      Arguments.of(
        true,
        List.of(msg(false), msg(false)),
        List.of(msg(false), msg(false))
      ),
      // 9
      Arguments.of(
        false,
        List.of(msg(true), msg(true)),
        List.of()
      ),
      // 10
      Arguments.of(
        false,
        List.of(msg(true), msg(false)),
        List.of()
      ),
      // 11
      Arguments.of(
        false,
        List.of(msg(false), msg(true)),
        List.of()
      ),
      // 12
      Arguments.of(
        false,
        List.of(msg(false), msg(false)),
        List.of()
      )
    );
  }
}
