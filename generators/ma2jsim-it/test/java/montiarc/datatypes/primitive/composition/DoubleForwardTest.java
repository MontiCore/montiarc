/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class DoubleForwardTest {

  /** Capture of the actual output stream on port pOut */
  @Captor ArgumentCaptor<Message<Number>> actual_outVal;


  /** The target port of output port o1 */
  @Mock InPort<Number> port_pOut;

  /**
   * @param input    the input stream on port pIn
   * @param expected the expected output stream on port pOut
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Double> input,
              @NotNull List<Double> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    DoubleForwardComp sut = new DoubleForwardCompBuilder().setName("sut").build();
    sut.port_pOut().connect(this.port_pOut);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_pOut).receive(this.actual_outVal.capture());

    // When
    sut.init();

    for (double msg : input) {
      sut.port_pIn().receive(Message.of(msg));
      sut.port_pIn().receive(Tick.get());
    }

    sut.run();

    // Then
    List<Double> actualAsBytes = this.actual_outVal.getAllValues().stream()
      .filter(m -> !(m instanceof Tick))
      .map(Message::getData)
      .map(Number::doubleValue)
      .collect(Collectors.toList());

    Assertions.assertThat(actualAsBytes).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.<Double>of(1.5),
        List.<Double>of(1.5)
      ),
      Arguments.of(
        List.<Double>of(1.5, 2.25, Double.MAX_VALUE, Double.MIN_NORMAL, Double.POSITIVE_INFINITY, Double.NaN, -0.0),
        List.<Double>of(1.5, 2.25, Double.MAX_VALUE, Double.MIN_NORMAL, Double.POSITIVE_INFINITY, Double.NaN, -0.0)
      )
    );
  }
}
