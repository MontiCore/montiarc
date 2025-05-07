/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.CardinalDirection;
import montiarc.types.Signal;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static montiarc.types.CardinalDirection.EAST;
import static montiarc.types.CardinalDirection.NORTH;
import static montiarc.types.CardinalDirection.SOUTH;
import static montiarc.types.CardinalDirection.WEST;
import static montiarc.types.Signal.SIGNAL;

@JSimTest
public class List1Test {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<CardinalDirection>> i1,
              @NotNull List<Message<Signal>> i2,
              @NotNull List<Message<CardinalDirection>> o) {
    Preconditions.checkNotNull(i1);
    Preconditions.checkNotNull(i2);
    Preconditions.checkNotNull(o);

    // Given
    List1Comp sut = new List1CompBuilder().setName("sut").build();
    PortObserver<CardinalDirection> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    for (Message<CardinalDirection> msg : i1) {
      sut.port_i().receive(msg);
    }
    for (Message<Signal> msg : i2) {
      sut.port_i2().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(o);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // 1
      Arguments.of(
        List.of(msg(NORTH), tk()),
        List.of(tk(), msg(SIGNAL)),
        List.of(tk(), msg(NORTH))
      ),
      // 2
      Arguments.of(
        List.of(msg(NORTH), tk()),
        List.of(tk()),
        List.of(tk())
      ),
      // 3
      Arguments.of(
        List.of(tk()),
        List.of(tk(), msg(SIGNAL)),
        List.of(tk())
      ),
      // 4
      Arguments.of(
        List.of(msg(NORTH), tk()),
        List.of(tk(), msg(SIGNAL), msg(SIGNAL)),
        List.of(tk(), msg(NORTH))
      ),
      // 5
      Arguments.of(
        List.of(msg(NORTH), msg(NORTH), tk()),
        List.of(tk(), msg(SIGNAL)),
        List.of(tk(), msg(NORTH))
      ),
      // 6
      Arguments.of(
        List.of(msg(NORTH), msg(NORTH), tk()),
        List.of(tk(), msg(SIGNAL), msg(SIGNAL)),
        List.of(tk(), msg(NORTH), msg(NORTH))
      ),
      // 7
      Arguments.of(
        List.of(msg(NORTH), msg(EAST), tk()),
        List.of(tk(), msg(SIGNAL), msg(SIGNAL)),
        List.of(tk(), msg(EAST), msg(NORTH))
      ),
      // 8
      Arguments.of(
        List.of(msg(NORTH), tk(), msg(EAST), tk()),
        List.of(tk(), tk(), msg(SIGNAL), msg(SIGNAL)),
        List.of(tk(), tk(), msg(EAST), msg(NORTH))
      ),
      // 9
      Arguments.of(
        List.of(msg(NORTH), tk(), tk(), msg(EAST), tk()),
        List.of(tk(), msg(SIGNAL), tk(), tk(), msg(SIGNAL)),
        List.of(tk(), msg(NORTH), tk(), tk(), msg(EAST))
      ),
      // 10
      Arguments.of(
        List.of(
          msg(NORTH), msg(EAST), msg(SOUTH), msg(WEST), tk()
        ),
        List.of(
          tk(), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL)
        ),
        List.of(
          tk(), msg(WEST), msg(SOUTH), msg(EAST), msg(NORTH)
        )
      )
    );
  }
}
