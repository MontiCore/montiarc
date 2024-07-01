/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.rte.port.ITimeAwareInPort;
import montiarc.rte.port.TimeAwarePortForComposition;
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
class UnusedPortsTest {

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
   * @param i1 the input stream on port i1
   * @param i2 the input stream on port i2
   * @param o1 the expected output stream on port o1
   * @param o2 the expected output stream on port o2
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Boolean>> i1,
              @NotNull List<Message<Boolean>> i2,
              @NotNull List<Message<Boolean>> o1,
              @NotNull List<Message<Boolean>> o2) {
    Preconditions.checkNotNull(i1);
    Preconditions.checkNotNull(i2);
    Preconditions.checkNotNull(o1);
    Preconditions.checkNotNull(o2);

    // Given
    UnusedPortsComp sut = new UnusedPortsCompBuilder().setName("sut").build();

    sut.port_o1().connect(this.port_o1);
    sut.port_o2().connect(this.port_o2);

    // when receiving a message, capture that message but do nothing else
    Mockito.lenient().doNothing().when(this.port_o1).receive(this.actual_o1.capture());
    Mockito.lenient().doNothing().when(this.port_o2).receive(this.actual_o2.capture());

    // When
    sut.init();

    for (Message<Boolean> msg : i1) {
      sut.port_i1().receive(msg);
    }

    for (Message<Boolean> msg : i2) {
      sut.port_i2().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(((TimeAwarePortForComposition<Boolean>) sut.port_i1()).getBuffer()).isEmpty();
    Assertions.assertThat(((TimeAwarePortForComposition<Boolean>) sut.port_i2()).getBuffer()).isEmpty();
    Assertions.assertThat(this.actual_o1.getAllValues()).containsExactlyElementsOf(o1);
    Assertions.assertThat(this.actual_o2.getAllValues()).containsExactlyElementsOf(o2);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(),
        List.of(),
        List.of(),
        List.of()
      ),
      Arguments.of(
        List.of(Tick.get()),
        List.of(Tick.get()),
        List.of(Tick.get()),
        List.of(Tick.get())
      ),
      Arguments.of(
        List.of(Tick.get(), Tick.get()),
        List.of(Tick.get(), Tick.get()),
        List.of(Tick.get(), Tick.get()),
        List.of(Tick.get(), Tick.get())
      ),
      Arguments.of(
        List.of(Tick.get(), Tick.get(), Tick.get()),
        List.of(Tick.get(), Tick.get(), Tick.get()),
        List.of(Tick.get(), Tick.get(), Tick.get()),
        List.of(Tick.get(), Tick.get(), Tick.get())
      )
    );
  }
}
