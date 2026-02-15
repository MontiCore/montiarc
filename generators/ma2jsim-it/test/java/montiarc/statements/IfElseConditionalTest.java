/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class IfElseConditionalTest {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(Message<Integer> i, Message<Integer> o) {
    // Given
    IfElseConditionalComp sut = new IfElseConditionalCompBuilder().setName("sut").build();

    PortObserver<Integer> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.port_i().receive(i);

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(o);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(msg(1), msg(1)),
      Arguments.of(msg(2), msg(2)),
      Arguments.of(msg(3), msg(3)),
      Arguments.of(msg(4), msg(4)),
      Arguments.of(msg(5), msg(5)),
      Arguments.of(msg(-1), msg(-1))
    );
  }
}
