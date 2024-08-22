/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.InPort;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;

@ExtendWith(MockitoExtension.class)
class ParamsBool5Test {

  /**
   * capture of the actual output stream on port o
   */
  @Captor
  ArgumentCaptor<Message<Boolean>> actual;

  /**
   * the target port of output port o
   */
  @Mock
  InPort<Boolean> port_o;

  /**
   * @param p1 the argument for parameter p1
   * @param p2 the argument for parameter p2
   */
  @ParameterizedTest
  @ValueSource(booleans = {
    true, true,
    true, false,
    false, true,
    false, false
  })
  @Disabled
  void testCtorSetsField(boolean p1, boolean p2) {
    // When
    ParamsBool5Comp sut = new ParamsBool5CompBuilder()
      .set_param_p1(p1)
      .set_param_p2(p2)
      .setName("sut").build();

    // Then
    //Assertions.assertThat(sut.field_v1).isEqualTo(p1);
    //Assertions.assertThat(sut.field_v2).isEqualTo(p2);
  }

  /**
   * @param p1 the argument for parameter p1
   * @param p2 the argument for parameter p2
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(boolean p1, boolean p2,
              @NotNull List<Message<Boolean>> input,
              @NotNull List<Message<Boolean>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    ParamsBool5Comp sut = new ParamsBool5CompBuilder()
      .set_param_p1(p1)
      .set_param_p2(p2)
      .setName("sut").build();

    sut.port_o().connect(this.port_o);

    // when receiving a message, capture that message but do nothing else
    Mockito.lenient().doNothing().when(this.port_o).receive(this.actual.capture());

    // When
    sut.init();

    for (Message<Boolean> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(this.actual.getAllValues()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // 1
      Arguments.of(
        true, true,
        List.of(msg(true)),
        List.of(msg(true))
      ),
      // 2
      Arguments.of(
        true, true,
        List.of(msg(false)),
        List.of(msg(true))
      ),
      // 3
      Arguments.of(
        true, false,
        List.of(msg(true)),
        List.of()
      ),
      // 4
      Arguments.of(
        true, false,
        List.of(msg(false)),
        List.of()
      ),
      // 5
      Arguments.of(
        false, true,
        List.of(msg(true)),
        List.of()
      ),
      // 6
      Arguments.of(
        false, true,
        List.of(msg(false)),
        List.of()
      ),
      // 7
      Arguments.of(
        false, false,
        List.of(msg(true)),
        List.of()
      ),
      // 8
      Arguments.of(
        false, false,
        List.of(msg(false)),
        List.of()
      ),
      // 9
      Arguments.of(
        true, true,
        List.of(msg(true), msg(true)),
        List.of(msg(true), msg(true))
      ),
      // 10
      Arguments.of(
        true, true,
        List.of(msg(true), msg(false)),
        List.of(msg(true), msg(true))
      ),
      // 11
      Arguments.of(
        true, true,
        List.of(msg(false), msg(true)),
        List.of(msg(true), msg(true))
      ),
      // 12
      Arguments.of(
        true, true,
        List.of(msg(false), msg(false)),
        List.of(msg(true), msg(true))
      ),
      // 13
      Arguments.of(
        true, false,
        List.of(msg(true), msg(true)),
        List.of()
      ),
      // 14
      Arguments.of(
        true, false,
        List.of(msg(true), msg(false)),
        List.of()
      ),
      // 15
      Arguments.of(
        true, false,
        List.of(msg(false), msg(true)),
        List.of()
      ),
      // 16
      Arguments.of(
        true, false,
        List.of(msg(false), msg(false)),
        List.of()
      ),
      // 17
      Arguments.of(
        false, true,
        List.of(msg(true), msg(true)),
        List.of()
      ),
      // 18
      Arguments.of(
        false, true,
        List.of(msg(true), msg(false)),
        List.of()
      ),
      // 19
      Arguments.of(
        false, true,
        List.of(msg(false), msg(true)),
        List.of()
      ),
      // 20
      Arguments.of(
        false, true,
        List.of(msg(false), msg(false)),
        List.of()
      ),
      // 21
      Arguments.of(
        false, false,
        List.of(msg(true), msg(true)),
        List.of()
      ),
      // 22
      Arguments.of(
        false, false,
        List.of(msg(true), msg(false)),
        List.of()
      ),
      // 23
      Arguments.of(
        false, false,
        List.of(msg(false), msg(true)),
        List.of()
      ),
      // 24
      Arguments.of(
        false, false,
        List.of(msg(false), msg(false)),
        List.of()
      )
    );
  }
}
