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
import org.junit.jupiter.api.Disabled;
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
@Disabled
public class List6Test {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Signal>> i,
              @NotNull List<Message<CardinalDirection>> o) {
    Preconditions.checkNotNull(i);
    Preconditions.checkNotNull(o);

    // Given
    List6Comp sut = new List6CompBuilder().setName("sut").build();
    PortObserver<CardinalDirection> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<Signal> msg : i) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(o);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // 1
      Arguments.of(
        List.of(msg(SIGNAL), tk()),
        List.of(tk())
      ),
      // 2
      Arguments.of(
        List.of(tk(), msg(SIGNAL)),
        List.of(tk(), msg(NORTH))
      ),
      // 3
      Arguments.of(
        List.of(tk(), msg(SIGNAL), msg(SIGNAL)),
        List.of(tk(), msg(NORTH), msg(EAST))
      ),
      // 4
      Arguments.of(
        List.of(tk(), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL)),
        List.of(tk(), msg(NORTH), msg(EAST), msg(SOUTH))
      ),
      // 5
      Arguments.of(
        List.of(tk(), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL)),
        List.of(tk(), msg(NORTH), msg(EAST), msg(SOUTH), msg(WEST))
      ),
      // 6
      Arguments.of(
        List.of(tk(), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL),
          msg(SIGNAL), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL)),
        List.of(tk(), msg(NORTH), msg(EAST), msg(SOUTH), msg(WEST))
      ),
      // 7
      Arguments.of(
        List.of(tk(), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL),
          tk(), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL)),
        List.of(tk(), msg(NORTH), msg(EAST), msg(SOUTH), msg(WEST),
          tk(), msg(NORTH), msg(EAST), msg(SOUTH), msg(WEST))
      ),
      // 8
      Arguments.of(
        List.of(tk(), tk(), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL),
          msg(SIGNAL), msg(SIGNAL), msg(SIGNAL), msg(SIGNAL)),
        List.of(tk(), tk(), msg(NORTH), msg(EAST), msg(SOUTH), msg(WEST))
      )
    );
  }
}
