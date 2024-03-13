/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ShortForwardTest {

  /** Capture of the actual output stream on port pOut */
  @Captor ArgumentCaptor<Message<Number>> actual_outVal;


  /** The target port of output port o1 */
  @Mock ITimeAwareInPort<Number> port_pOut;

  /**
   * @param input    the input stream on port pIn
   * @param expected the expected output stream on port pOut
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Short> input,
              @NotNull List<Short> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    ShortForwardComp sut = new ShortForwardCompBuilder().setScheduler(new InstantSchedule()).setName("sut").build();
    sut.port_pOut().connect(this.port_pOut);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_pOut).receive(this.actual_outVal.capture());

    // When
    sut.init();

    for (short msg : input) {
      sut.port_pIn().receive(Message.of(msg));
      sut.port_pIn().receive(Tick.get());
    }

    // Then
    List<Short> actualAsBytes = this.actual_outVal.getAllValues().stream()
      .filter(m -> !(m instanceof Tick))
      .map(Message::getData)
      .map(Number::shortValue)
      .collect(Collectors.toList());

    Assertions.assertThat(actualAsBytes).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.<Short>of((short) 1),
        List.<Short>of((short) 1)
      ),
      Arguments.of(
        List.<Short>of((short) 1, (short) 2, Short.MAX_VALUE),
        List.<Short>of((short) 1, (short) 2, Short.MAX_VALUE)
      )
    );
  }
}
