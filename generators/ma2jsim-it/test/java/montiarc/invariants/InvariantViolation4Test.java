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
class InvariantViolation4Test {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(int errorCount,
              @NotNull List<Message<Integer>> input,
              @NotNull List<Message<Integer>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);
    Preconditions.checkArgument(errorCount >= 0);

    // Given
    InvariantViolation4Comp sut = new InvariantViolation4CompBuilder().setName("sut").build();
    PortObserver<Integer> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);
    // When

    sut.init();

    for (Message<Integer> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

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
        List.of(msg(11))
      ),
      // 2
      Arguments.of(
        1,
        List.of(msg(2)),
        List.of(msg(11), msg(21))
      ),
      // 3
      Arguments.of(
        1,
        List.of(msg(21)),
        List.of(msg(11), msg(21))
      ),
      // 4
      Arguments.of(
        1,
        List.of(msg(22)),
        List.of(msg(11), msg(22))
      ),
      // 5
      Arguments.of(
        1,
        List.of(msg(3)),
        List.of(msg(11), msg(31))
      ),
      // 6
      Arguments.of(
        1,
        List.of(msg(31)),
        List.of(msg(11), msg(31))
      ),
      // 7
      Arguments.of(
        2,
        List.of(msg(32)),
        List.of(msg(11), msg(32))
      ),
      // 8
      Arguments.of(
        0,
        List.of(msg(4)),
        List.of(msg(11), msg(41))
      ),
      // 9
      Arguments.of(
        1,
        List.of(msg(4), tk()),
        List.of(msg(11), msg(41), tk())
      ),
      // 10
      Arguments.of(
        2,
        List.of(msg(4), tk(), tk()),
        List.of(msg(11), msg(41), tk(), tk())
      ),
      // 11
      Arguments.of(
        0,
        List.of(msg(5)),
        List.of(msg(11), msg(51))
      ),
      // 12
      Arguments.of(
        1,
        List.of(msg(5), tk()),
        List.of(msg(11), msg(51), tk())
      ),
      // 13
      Arguments.of(
        2,
        List.of(msg(5), tk(), tk()),
        List.of(msg(11), msg(51), tk(), tk())
      ),
      // 14
      Arguments.of(
        0,
        List.of(msg(6)),
        List.of(msg(11), msg(61))
      ),
      // 15
      Arguments.of(
        1,
        List.of(msg(6), tk()),
        List.of(msg(11), msg(61), tk())
      ),
      // 16
      Arguments.of(
        2,
        List.of(msg(6), tk(), tk()),
        List.of(msg(11), msg(61), tk(), tk())
      )
    );
  }
}
