/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.CardinalDirection;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.types.CardinalDirection.*;

@JSimTest
public class List2Test {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<CardinalDirection> p,
              @NotNull List<Message<Integer>> i,
              @NotNull List<Message<CardinalDirection>> o) {
    Preconditions.checkNotNull(p);
    Preconditions.checkNotNull(i);
    Preconditions.checkNotNull(o);

    // Given
    List2Comp sut = new List2CompBuilder().set_param_p(p).setName("sut").build();
    PortObserver<CardinalDirection> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    for (Message<Integer> msg : i) {
      sut.port_i().receive(msg);
    }

    sut.runToCompletion();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(o);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // 1
      Arguments.of(
        List.of(),
        List.of(msg(0)),
        List.of()
      ),
      // 2
      Arguments.of(
        List.of(),
        List.of(msg(Integer.MIN_VALUE)),
        List.of()
      ),
      // 3
      Arguments.of(
        List.of(),
        List.of(msg(Integer.MAX_VALUE)),
        List.of()
      ),
      // 4
      Arguments.of(
        List.of(NORTH),
        List.of(),
        List.of()
      ),
      // 5
      Arguments.of(
        List.of(NORTH),
        List.of(msg(0)),
        List.of(msg(NORTH))
      ),
      // 6
      Arguments.of(
        List.of(NORTH),
        List.of(msg(-1)),
        List.of()
      ),
      // 7
      Arguments.of(
        List.of(NORTH),
        List.of(msg(1)),
        List.of()
      ),
      // 8
      Arguments.of(
        List.of(NORTH),
        List.of(msg(0), msg(0)),
        List.of(msg(NORTH), msg(NORTH))
      ),
      // 9
      Arguments.of(
        List.of(NORTH, EAST),
        List.of(msg(0), msg(1)),
        List.of(msg(NORTH), msg(EAST))
      ),
      // 10
      Arguments.of(
        List.of(NORTH, EAST),
        List.of(msg(1), msg(0)),
        List.of(msg(EAST), msg(NORTH))
      ),
      // 11
      Arguments.of(
        List.of(NORTH, EAST, SOUTH, WEST),
        List.of(msg(0), msg(1), msg(2), msg(3)),
        List.of(msg(NORTH), msg(EAST), msg(SOUTH), msg(WEST))
      ),
      // 12
      Arguments.of(
        List.of(NORTH, EAST, SOUTH, WEST),
        List.of(msg(0), msg(1), msg(3), msg(2)),
        List.of(msg(NORTH), msg(EAST), msg(WEST), msg(SOUTH))
      ),
      // 13
      Arguments.of(
        List.of(NORTH, EAST, SOUTH, WEST),
        List.of(msg(0), msg(2), msg(1), msg(3)),
        List.of(msg(NORTH), msg(SOUTH), msg(EAST), msg(WEST))
      ),
      // 14
      Arguments.of(
        List.of(NORTH, EAST, SOUTH, WEST),
        List.of(msg(0), msg(2), msg(3), msg(1)),
        List.of(msg(NORTH), msg(SOUTH), msg(WEST), msg(EAST))
      ),
      // 15
      Arguments.of(
        List.of(NORTH, EAST, SOUTH, WEST),
        List.of(msg(0), msg(3), msg(1), msg(2)),
        List.of(msg(NORTH), msg(WEST), msg(EAST), msg(SOUTH))
      ),
      // 16
      Arguments.of(
        List.of(NORTH, EAST, SOUTH, WEST),
        List.of(msg(0), msg(3), msg(2), msg(1)),
        List.of(msg(NORTH), msg(WEST), msg(SOUTH), msg(EAST))
      )
    );
  }
}
