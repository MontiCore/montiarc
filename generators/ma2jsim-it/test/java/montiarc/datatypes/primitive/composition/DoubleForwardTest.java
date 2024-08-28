/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.tk;

class DoubleForwardTest {

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
    PortObserver<Number> port_o = new PortObserver<>();

    sut.port_pOut().connect(port_o);

    // When
    sut.init();

    for (double msg : input) {
      sut.port_pIn().receive(Message.of(msg));
      sut.port_pIn().receive(tk());
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedValues()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(1.5),
        List.of(1.5)
      ),
      Arguments.of(
        List.of(1.5, 2.25, Double.MAX_VALUE, Double.MIN_NORMAL, Double.POSITIVE_INFINITY, Double.NaN, -0.0),
        List.of(1.5, 2.25, Double.MAX_VALUE, Double.MIN_NORMAL, Double.POSITIVE_INFINITY, Double.NaN, -0.0)
      )
    );
  }
}
