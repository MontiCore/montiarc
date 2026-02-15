/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.params.DisableIfAnyArgument;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class WhileLoopTest {

  @ParameterizedTest
  @MethodSource("io")
  @DisableIfAnyArgument(matches = "3")
  void testIO(Message<Integer> i, List<Message<Integer>> o) {
    // Given
    WhileLoopComp sut = new WhileLoopCompBuilder().setName("sut").build();

    PortObserver<Integer> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.port_i().receive(i);

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(o);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        msg(1), List.of(
          msg(0), msg(1), msg(2), msg(3), msg(4),
          msg(5), msg(6), msg(7), msg(8), msg(9)
        )
      ),
      Arguments.of(
        msg(2), List.of(
          msg(0), msg(1), msg(2), msg(3), msg(4),
          msg(5), msg(6), msg(7), msg(8), msg(9)
        )
      ),
      Arguments.of(
        msg(3), List.of(
          msg(0), msg(1), msg(2), msg(3), msg(4),
          msg(5), msg(6), msg(7), msg(8), msg(9)
        )
      )
    );
  }
}
