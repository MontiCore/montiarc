/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

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
public class DeployTest {

  /**
   * capture of the actual output stream on port o
   */
  @Captor
  ArgumentCaptor<Message<OnOff>> actual1;

  /**
   * capture of the actual output stream on port o
   */
  @Captor
  ArgumentCaptor<Message<OnOff>> actual2;

  /**
   * the target port of output port o
   */
  @Mock
  ITimeAwareInPort<OnOff> port_1;

  /**
   * the target port of output port o
   */
  @Mock
  ITimeAwareInPort<OnOff> port_2;

  /**
   * @param expected1 the input stream on port i
   * @param expected2 the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("expected")
  void testIO(@NotNull List<Message<OnOff>> expected1,
              @NotNull List<Message<OnOff>> expected2) {
    Preconditions.checkNotNull(expected1);
    Preconditions.checkNotNull(expected2);

    // Given
    final DeployComp sut = new DeployCompBuilder("sut").build();

    sut.subcomp_parallel().port_o1().connect(this.port_1);
    sut.subcomp_parallel().port_o2().connect(this.port_2);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_1).receive(this.actual1.capture());
    Mockito.doNothing().when(this.port_2).receive(this.actual2.capture());

    // When
    sut.init();
    sut.run(3);

    // Then
    Assertions.assertThat(this.actual1.getAllValues()).as("parallel.o1").containsExactlyElementsOf(expected1);
    Assertions.assertThat(this.actual2.getAllValues()).as("parallel.o2").containsExactlyElementsOf(expected2);
  }

  static Stream<Arguments> expected() {
    return Stream.of(
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get()),
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get())
      ));
  }
}