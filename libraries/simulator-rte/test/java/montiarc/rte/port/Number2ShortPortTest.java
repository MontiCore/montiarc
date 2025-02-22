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

public class Number2ShortPortTest extends TestBase {

  @ParameterizedTest
  @MethodSource("io")
  <T extends Number> void test(T in, Short out) {
    // Given
    Number2ShortPort<T> port_i = new Number2ShortPort<>("", null);
    PortObserver<Short> port_o = new PortObserver<>();
    port_i.connect(port_o);

    // When
    port_i.receive(msg(in));

    // Then
    assertThat(port_o.getObservedMessages()).containsExactlyInAnyOrder(msg(out));
  }

  static Stream<Arguments> io() {
    return Stream.of(
        arguments((byte) Short.MIN_VALUE, (short) (byte) Short.MIN_VALUE),
        arguments((byte) Short.MAX_VALUE, (short) (byte) Short.MAX_VALUE),
        arguments(Short.MIN_VALUE, Short.MIN_VALUE),
        arguments(Short.MAX_VALUE, Short.MAX_VALUE),
        arguments((int) Short.MIN_VALUE, Short.MIN_VALUE),
        arguments((int) Short.MAX_VALUE, Short.MAX_VALUE),
        arguments((long) Short.MIN_VALUE, Short.MIN_VALUE),
        arguments((long) Short.MAX_VALUE, Short.MAX_VALUE),
        arguments((float) Short.MIN_VALUE, Short.MIN_VALUE),
        arguments((float) Short.MAX_VALUE, Short.MAX_VALUE),
        arguments((double) Short.MIN_VALUE, Short.MIN_VALUE),
        arguments((double) Short.MAX_VALUE, Short.MAX_VALUE)
    );
  }
}
