/* (c) https://github.com/MontiCore/monticore */
package montiarc.invariants;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class InvariantValidation1Test {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Number>> i,
              @NotNull List<Message<Number>> o) {
    Preconditions.checkNotNull(i);
    Preconditions.checkNotNull(o);

    // Given
    InvariantValidation1Comp sut = new InvariantValidation1CompBuilder().setName("sut").build();
    PortObserver<Number> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);
    // When

    sut.init();

    for (Message<Number> msg : i) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(o);
    assertThat(Log.getFindings()).isEmpty();
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // 1
      Arguments.of(
        List.of(),
        List.of(msg(1))
      ),
      // 2
      Arguments.of(
        List.of(msg(1)),
        List.of(msg(1), msg(1))
      ),
      // 3
      Arguments.of(
        List.of(msg(2)),
        List.of(msg(1), msg(2))
      ),
      // 4
      Arguments.of(
        List.of(msg(3)),
        List.of(msg(1), msg(3))
      ),
      // 5
      Arguments.of(
        List.of(msg(2), msg(3)),
        List.of(msg(1), msg(2), msg(3))
      ),
      // 6
      Arguments.of(
        List.of(msg(1), msg(1)),
        List.of(msg(1), msg(1), msg(1))
      ),
      // 7
      Arguments.of(
        List.of(msg(2), msg(2)),
        List.of(msg(1), msg(2), msg(2))
      ),
      // 8
      Arguments.of(
        List.of(msg(3), msg(3)),
        List.of(msg(1), msg(3), msg(3))
      ),
      // 9
      Arguments.of(
        List.of(msg(1), tk(), msg(2), tk(), msg(3)),
        List.of(msg(1), msg(1), tk(), msg(2), tk(), msg(3))
      )
    );
  }
}
