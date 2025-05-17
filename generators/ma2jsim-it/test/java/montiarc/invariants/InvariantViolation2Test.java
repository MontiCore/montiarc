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
class InvariantViolation2Test {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(int errorCount,
              @NotNull List<Message<Integer>> input,
              @NotNull List<Message<Integer>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);
    Preconditions.checkArgument(errorCount >= 0);

    // Given
    InvariantViolation2Comp sut = new InvariantViolation2CompBuilder().setName("sut").build();
    PortObserver<Integer> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    for (Message<Integer> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
    assertThat(Log.getFindings().size()).as(() -> Log.getFindings().toString()).isEqualTo(errorCount);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // 1
      Arguments.of(
        0,
        List.of(),
        List.of(msg(1))
      ),
      // 2
      Arguments.of(
        1,
        List.of(msg(2)),
        List.of(msg(1), msg(2))
      ),
      // 3
      Arguments.of(
        1,
        List.of(msg(3)),
        List.of(msg(1), msg(3))
      ),
      // 4
      Arguments.of(
        0,
        List.of(msg(4)),
        List.of(msg(1), msg(4))
      ),
      // 5
      Arguments.of(
        1,
        List.of(msg(4), tk()),
        List.of(msg(1), msg(4), tk())
      ),
      // 6
      Arguments.of(
        0,
        List.of(tk(), msg(4)),
        List.of(msg(1), tk(), msg(4))
      ),
      // 7
      Arguments.of(
        2,
        List.of(msg(4), tk(), tk()),
        List.of(msg(1), msg(4), tk(), tk())
      )
    );
  }
}
