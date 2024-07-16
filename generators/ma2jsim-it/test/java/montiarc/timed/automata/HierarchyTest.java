/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.ITimeAwareInPort;
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
class HierarchyTest {

  /**
   * capture of the actual output stream on port o
   */
  @Captor
  ArgumentCaptor<Message<String>> actual;

  /**
   * the target port of output port o
   */
  @Mock
  ITimeAwareInPort<String> port_o;

  /**
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Integer>> input,
              @NotNull List<Message<String>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    HierarchyComp sut = new HierarchyCompBuilder().setName("sut").build();

    sut.port_o().connect(this.port_o);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_o).receive(this.actual.capture());

    // When
    sut.init();

    for (Message<Integer> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(this.actual.getAllValues()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(msg(1), tk()), // input
        List.of(msg("first"), tk()) // expected
      ),
      Arguments.of(
        List.of(tk()), // input
        List.of(tk()) // expected
      ),
      Arguments.of(
        List.of(msg(2), tk()), // input
        List.of(msg("second"), tk()) // expected
      ),
      Arguments.of(
        List.of(msg(3), tk()), // input
        List.of(msg("third"), tk()) // expected
      ),
      Arguments.of(
        List.of(msg(4), tk()), // input
        List.of(msg("fourth"), tk()) // expected
      ),
      Arguments.of(
        List.of(msg(5), tk()), // input
        List.of(msg("fifth"), tk()) // expected
      ),
      Arguments.of(
        List.of(msg(2), tk(), msg(3), tk()), // input
        List.of(msg("second"), tk(), msg("third"), tk()) // expected
      ),
      Arguments.of(
        List.of(msg(1), tk(), msg(6), tk()), // input
        List.of(msg("first"), tk(), msg("sixth"), tk()) // expected
      )
    );
  }
}