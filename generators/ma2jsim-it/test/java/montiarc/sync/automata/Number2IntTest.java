/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.rte.port.InPort;
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

@ExtendWith(MockitoExtension.class)
class Number2IntTest {

  /**
   * capture of the actual output stream on port o
   */
  @Captor
  ArgumentCaptor<Message<Number>> actual;

  /**
   * the target port of output port o
   */
  @Mock
  InPort<Number> port_o;

  /**
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Number>> input,
              @NotNull List<Message<Number>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    Number2IntComp<Number> sut = new Number2IntCompBuilder<>().setName("sut").build();

    sut.port_o().connect(this.port_o);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_o).receive(this.actual.capture());

    // When
    sut.init();

    for (Message<Number> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(this.actual.getAllValues()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(new Message<>(0), Tick.get()),
        List.of(new Message<>(0), Tick.get())
      ),
      Arguments.of(
        List.of(new Message<>(1.5), Tick.get()),
        List.of(new Message<>(1), Tick.get())
      ),
      Arguments.of(
        List.of(new Message<>(1.5), Tick.get(), new Message<>(-1), Tick.get()),
        List.of(new Message<>(1), Tick.get(), new Message<>(-1), Tick.get())
      ),
      Arguments.of(
        List.of(new Message<>(1F), Tick.get(), new Message<>(3.333333), Tick.get(), new Message<>(-1.6F), Tick.get()),
        List.of(new Message<>(1), Tick.get(), new Message<>(3), Tick.get(), new Message<>(-1), Tick.get())
      ));
  }
}
