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

public class Number2LongPortTest extends TestBase {

  @ParameterizedTest
  @MethodSource("io")
  <T extends Number> void test(T in, Long out) {
    // Given
    Number2LongPort<T> port_i = new Number2LongPort<>("", null);
    PortObserver<Long> port_o = new PortObserver<>();
    port_i.connect(port_o);

    // When
    port_i.receive(msg(in));

    // Then
    assertThat(port_o.getObservedMessages()).containsExactlyInAnyOrder(msg(out));
  }

  static Stream<Arguments> io() {
    return Stream.of(
        arguments((byte) Long.MIN_VALUE, (long) (byte) Long.MIN_VALUE),
        arguments((byte) Long.MAX_VALUE, (long) (byte) Long.MAX_VALUE),
        arguments((short) Long.MIN_VALUE, (long) (short) Long.MIN_VALUE),
        arguments((short) Long.MAX_VALUE, (long) (short) Long.MAX_VALUE),
        arguments((int) Long.MIN_VALUE, (long) (int) Long.MIN_VALUE),
        arguments((int) Long.MAX_VALUE, (long) (int) Long.MAX_VALUE),
        arguments(Long.MIN_VALUE, Long.MIN_VALUE),
        arguments(Long.MAX_VALUE, Long.MAX_VALUE),
        arguments((float) Long.MIN_VALUE, Long.MIN_VALUE),
        arguments((float) Long.MAX_VALUE, Long.MAX_VALUE),
        arguments((double) Long.MIN_VALUE, Long.MIN_VALUE),
        arguments((double) Long.MAX_VALUE, Long.MAX_VALUE)
    );
  }
}
