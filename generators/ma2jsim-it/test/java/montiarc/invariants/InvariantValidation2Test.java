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
import static org.assertj.core.api.Assertions.assertThat;


@JSimTest
class InvariantValidation2Test {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Integer>> input,
              @NotNull List<Message<Integer>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    InvariantValidation2Comp sut = new InvariantValidation2CompBuilder().setName("sut").build();
    PortObserver<Integer> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    for (Message<Integer> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
    assertThat(Log.getFindings()).isEmpty();
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // 1
      Arguments.of(
        List.of(),
        List.of(msg(11))
      ),
      // 2
      Arguments.of(
        List.of(msg(2)),
        List.of(msg(11), msg(21))
      ),
      // 3
      Arguments.of(
        List.of(msg(21)),
        List.of(msg(11), msg(21))
      ),
      // 4
      Arguments.of(
        List.of(msg(22)),
        List.of(msg(11), msg(22))
      ),
      /*// 5
      Arguments.of(
        List.of(msg(2), msg(1)),
        List.of(msg(11), msg(21), msg(11))
      ),*/
      // 6
      Arguments.of(
        List.of(msg(2), msg(21)),
        List.of(msg(11), msg(21), msg(21))
      ),
      // 7
      Arguments.of(
        List.of(msg(2), msg(22)),
        List.of(msg(11), msg(21), msg(22))
      )
      // 8
      /*Arguments.of(
        List.of(msg(21), msg(10)),
        List.of(msg(11), msg(21), msg(11))
      ),*/
      // 9
      /*Arguments.of(
        List.of(msg(22), msg(10)),
        List.of(msg(11), msg(22), msg(11))
      )*/
    );
  }
}
