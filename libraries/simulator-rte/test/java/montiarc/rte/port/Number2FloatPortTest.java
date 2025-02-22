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

public class Number2FloatPortTest extends TestBase {

  @ParameterizedTest
  @MethodSource("io")
  <T extends Number> void test(T in, Float out) {
    // Given
    Number2FloatPort<T> port_i = new Number2FloatPort<>("", null);
    PortObserver<Float> port_o = new PortObserver<>();
    port_i.connect(port_o);

    // When
    port_i.receive(msg(in));

    // Then
    assertThat(port_o.getObservedMessages()).containsExactlyInAnyOrder(msg(out));
  }

  @SuppressWarnings("ConstantValue")
  static Stream<Arguments> io() {
    return Stream.of(
        arguments((byte) Float.MIN_VALUE, (float) (byte) Float.MIN_VALUE),
        arguments((byte) Float.MAX_VALUE, (float) (byte) Float.MAX_VALUE),
        arguments((short) Float.MIN_VALUE, (float) (short) Float.MIN_VALUE),
        arguments((short) Float.MAX_VALUE, (float) (short) Float.MAX_VALUE),
        arguments((int) Float.MIN_VALUE, (float) (int) Float.MIN_VALUE),
        arguments((int) Float.MAX_VALUE, (float) (int) Float.MAX_VALUE),
        arguments((long) Float.MIN_VALUE, (float) (long) Float.MIN_VALUE),
        arguments((long) Float.MAX_VALUE, (float) (long) Float.MAX_VALUE),
        arguments(Float.MIN_VALUE, Float.MIN_VALUE),
        arguments(Float.MAX_VALUE, Float.MAX_VALUE),
        arguments((double) Float.MIN_VALUE, Float.MIN_VALUE),
        arguments((double) Float.MAX_VALUE, Float.MAX_VALUE)
    );
  }
}
