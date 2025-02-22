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

public class Number2IntegerPortTest extends TestBase {

  @ParameterizedTest
  @MethodSource("io")
  <T extends Number> void test(T in, Integer out) {
    // Given
    Number2IntegerPort<T> port_i = new Number2IntegerPort<>("", null);
    PortObserver<Integer> port_o = new PortObserver<>();
    port_i.connect(port_o);

    // When
    port_i.receive(msg(in));

    // Then
    assertThat(port_o.getObservedMessages()).containsExactlyInAnyOrder(msg(out));
  }

  static Stream<Arguments> io() {
    return Stream.of(
        arguments((byte) Integer.MIN_VALUE, (int) (byte) Integer.MIN_VALUE),
        arguments((byte) Integer.MAX_VALUE, (int) (byte) Integer.MAX_VALUE),
        arguments((short) Integer.MIN_VALUE, (int) (short) Integer.MIN_VALUE),
        arguments((short) Integer.MAX_VALUE, (int) (short) Integer.MAX_VALUE),
        arguments(Integer.MIN_VALUE, Integer.MIN_VALUE),
        arguments(Integer.MAX_VALUE, Integer.MAX_VALUE),
        arguments((long) Integer.MIN_VALUE, Integer.MIN_VALUE),
        arguments((long) Integer.MAX_VALUE, Integer.MAX_VALUE),
        arguments((float) Integer.MIN_VALUE, Integer.MIN_VALUE),
        arguments((float) Integer.MAX_VALUE, Integer.MAX_VALUE),
        arguments((double) Integer.MIN_VALUE, Integer.MIN_VALUE),
        arguments((double) Integer.MAX_VALUE, Integer.MAX_VALUE)
    );
  }
}
