/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.rte.port.ITimeAwareInPort;
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

@ExtendWith(MockitoExtension.class)
class NameOverlapTest {

  /**
   * capture of the actual output stream on port o
   */
  @Captor
  ArgumentCaptor<Message<OnOff>> actual0;

  /**
   * the target port of output port o
   */
  @Mock
  ITimeAwareInPort<OnOff> port_o0;

  /**
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    NameOverlapComp sut = new NameOverlapCompBuilder().setName("sut").set_feature_onOff(true).build();

    sut.port_o0().connect(this.port_o0);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_o0).receive(this.actual0.capture());

    // When
    sut.init();
    sut.getScheduler().run((int) expected.stream().filter(msg -> !msg.equals(Tick.get())).count());

    // Then
    Assertions.assertThat(this.actual0.getAllValues()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), Tick.get())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get())
      ));
  }

  /**
   * capture of the actual output stream on port o
   */
  @Captor
  ArgumentCaptor<Message<Integer>> actual1;

  /**
   * the target port of output port o
   */
  @Mock
  ITimeAwareInPort<Integer> port_o1;

  /**
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("ioInt")
  void testIOInt(@NotNull List<Message<Integer>> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    NameOverlapComp sut = new NameOverlapCompBuilder().setName("sut").set_feature_onOff(false).build();

    sut.port_o1().connect(this.port_o1);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_o1).receive(this.actual1.capture());

    // When
    sut.init();
    sut.getScheduler().run((int) expected.stream().filter(msg -> !msg.equals(Tick.get())).count());

    // Then
    Assertions.assertThat(this.actual1.getAllValues()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> ioInt() {
    return Stream.of(
      Arguments.of(
        List.of(new Message<>(0), Tick.get())
      ),
      Arguments.of(
        List.of(new Message<>(0), Tick.get(), new Message<>(0), Tick.get())
      ),
      Arguments.of(
        List.of(new Message<>(0), Tick.get(), new Message<>(0), Tick.get(), new Message<>(0), Tick.get())
      ));
  }
}
