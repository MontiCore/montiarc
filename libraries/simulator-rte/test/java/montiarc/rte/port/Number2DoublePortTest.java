/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.TestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class Number2DoublePortTest extends TestBase {

  @ParameterizedTest
  @MethodSource("io")
  <T extends Number> void test(T in, Double out) {
    // Given
    Number2DoublePort<T> port_i = new Number2DoublePort<>("", null);
    PortObserver<Double> port_o = new PortObserver<>();
    port_i.connect(port_o);

    // When
    port_i.receive(msg(in));

    // Then
    assertThat(port_o.getObservedMessages()).containsExactlyInAnyOrder(msg(out));
  }

  @SuppressWarnings("ConstantValue")
  static Stream<Arguments> io() {
    return Stream.of(
        arguments((byte) Double.MIN_VALUE, (double) (byte) Double.MIN_VALUE),
        arguments((byte) Double.MAX_VALUE, (double) (byte) Double.MAX_VALUE),
        arguments((short) Double.MIN_VALUE, (double) (byte) Double.MIN_VALUE),
        arguments((short) Double.MAX_VALUE, (double) (short) Double.MAX_VALUE),
        arguments((int) Double.MIN_VALUE, (double) (byte) Double.MIN_VALUE),
        arguments((int) Double.MAX_VALUE, (double) (int) Double.MAX_VALUE),
        arguments((long) Double.MIN_VALUE, (double) (byte) Double.MIN_VALUE),
        arguments((long) Double.MAX_VALUE, (double) (long) Double.MAX_VALUE),
        arguments((float) Double.MIN_VALUE, (double) (float) Double.MIN_VALUE),
        arguments((float) Double.MAX_VALUE, (double) (float) Double.MAX_VALUE),
        arguments(Double.MIN_VALUE, Double.MIN_VALUE),
        arguments(Double.MAX_VALUE, Double.MAX_VALUE)
    );
  }
}
