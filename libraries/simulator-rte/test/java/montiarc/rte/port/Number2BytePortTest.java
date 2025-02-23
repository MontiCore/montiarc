/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import de.se_rwth.commons.Symbol;
import montiarc.TestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class Number2BytePortTest extends TestBase {

  @ParameterizedTest
  @MethodSource("io")
  <T extends Number> void test(T in, Byte out) {
    // Given
    Number2BytePort<T> port_i = new Number2BytePort<>("", null);
    PortObserver<Byte> port_o = new PortObserver<>();
    port_i.connect(port_o);

    // When
    port_i.receive(msg(in));

    // Then
    assertThat(port_o.getObservedMessages()).containsExactlyInAnyOrder(msg(out));
  }

  static Stream<Arguments> io() {
    return Stream.of(
        arguments(Byte.MIN_VALUE, Byte.MIN_VALUE),
        arguments(Byte.MAX_VALUE, Byte.MAX_VALUE),
        arguments((short) Byte.MIN_VALUE, Byte.MIN_VALUE),
        arguments((short) Byte.MAX_VALUE, Byte.MAX_VALUE),
        arguments((int) Byte.MIN_VALUE, Byte.MIN_VALUE),
        arguments((int) Byte.MAX_VALUE, Byte.MAX_VALUE),
        arguments((long) Byte.MIN_VALUE, Byte.MIN_VALUE),
        arguments((long) Byte.MAX_VALUE, Byte.MAX_VALUE),
        arguments((float) Byte.MIN_VALUE, Byte.MIN_VALUE),
        arguments((float) Byte.MAX_VALUE, Byte.MAX_VALUE),
        arguments((double) Byte.MIN_VALUE, Byte.MIN_VALUE),
        arguments((double) Byte.MAX_VALUE, Byte.MAX_VALUE)
    );
  }
}
