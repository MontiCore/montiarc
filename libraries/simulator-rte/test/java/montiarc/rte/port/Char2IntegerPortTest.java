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

class Char2IntegerPortTest extends TestBase {

  @ParameterizedTest
  @MethodSource("io")
  void test(Character in, Integer out) {
    // Given
    Char2IntegerPort port_i = new Char2IntegerPort("", null);
    PortObserver<Integer> port_o = new PortObserver<>();
    port_i.connect(port_o);

    // When
    port_i.receive(msg(in));

    // Then
    assertThat(port_o.getObservedMessages()).containsExactlyInAnyOrder(msg(out));
  }

  static Stream<Arguments> io() {
    return Stream.of(
      arguments(Character.MIN_VALUE, (int) Character.MIN_VALUE),
      arguments(Character.MAX_VALUE, (int) Character.MAX_VALUE)
    );
  }
}
