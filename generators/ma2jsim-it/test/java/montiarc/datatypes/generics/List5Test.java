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
import static montiarc.types.CardinalDirection.EAST;
import static montiarc.types.CardinalDirection.NORTH;
import static montiarc.types.CardinalDirection.SOUTH;
import static montiarc.types.CardinalDirection.WEST;

@JSimTest
public class List5Test {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Integer>> i,
              @NotNull List<Message<CardinalDirection>> o0,
              @NotNull List<Message<CardinalDirection>> o1,
              @NotNull List<Message<CardinalDirection>> o2,
              @NotNull List<Message<CardinalDirection>> o3,
              @NotNull List<Message<CardinalDirection>> o4) {
    Preconditions.checkNotNull(i);
    Preconditions.checkNotNull(o0);
    Preconditions.checkNotNull(o1);
    Preconditions.checkNotNull(o2);
    Preconditions.checkNotNull(o3);
    Preconditions.checkNotNull(o4);

    // Given
    List5Comp sut = new List5CompBuilder().setName("sut").build();
    PortObserver<CardinalDirection> port_o0 = new PortObserver<>();
    PortObserver<CardinalDirection> port_o1 = new PortObserver<>();
    PortObserver<CardinalDirection> port_o2 = new PortObserver<>();
    PortObserver<CardinalDirection> port_o3 = new PortObserver<>();
    PortObserver<CardinalDirection> port_o4 = new PortObserver<>();

    sut.port_o0().connect(port_o0);
    sut.port_o1().connect(port_o1);
    sut.port_o2().connect(port_o2);
    sut.port_o3().connect(port_o3);
    sut.port_o4().connect(port_o4);

    // When
    sut.init();

    for (Message<Integer> msg : i) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o0.getObservedMessages()).containsExactlyElementsOf(o0);
    Assertions.assertThat(port_o1.getObservedMessages()).containsExactlyElementsOf(o1);
    Assertions.assertThat(port_o2.getObservedMessages()).containsExactlyElementsOf(o2);
    Assertions.assertThat(port_o3.getObservedMessages()).containsExactlyElementsOf(o3);
    Assertions.assertThat(port_o4.getObservedMessages()).containsExactlyElementsOf(o4);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // 1
      Arguments.of(
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        List.of()
      ),
      // 2
      Arguments.of(
        List.of(msg(0)),
        List.of(),
        List.of(msg(NORTH)),
        List.of(msg(NORTH)),
        List.of(msg(NORTH)),
        List.of(msg(NORTH))
      ),
      // 3
      Arguments.of(
        List.of(msg(-1)),
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        List.of()
      ),
      // 4
      Arguments.of(
        List.of(msg(1)),
        List.of(),
        List.of(),
        List.of(msg(EAST)),
        List.of(msg(EAST)),
        List.of(msg(EAST))
      ),
      // 5
      Arguments.of(
        List.of(msg(2)),
        List.of(),
        List.of(),
        List.of(),
        List.of(msg(SOUTH)),
        List.of(msg(SOUTH))
      ),
      // 6
      Arguments.of(
        List.of(msg(3)),
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        List.of(msg(WEST))
      ),
      // 7
      Arguments.of(
        List.of(msg(Integer.MIN_VALUE)),
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        List.of()
      ),
      // 8
      Arguments.of(
        List.of(msg(Integer.MAX_VALUE)),
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        List.of()
      ),
      // 9
      Arguments.of(
        List.of(msg(-1), msg(0), msg(1), msg(2), msg(3)),
        List.of(),
        List.of(msg(NORTH)),
        List.of(msg(NORTH), msg(EAST)),
        List.of(msg(NORTH), msg(EAST), msg(SOUTH)),
        List.of(msg(NORTH), msg(EAST), msg(SOUTH), msg(WEST))
      )
    );
  }
}
