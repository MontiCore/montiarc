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

@JSimTest
public class List3Test {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Integer>> i,
              @NotNull List<Message<CardinalDirection>> o) {
    Preconditions.checkNotNull(i);
    Preconditions.checkNotNull(o);

    // Given
    List3Comp sut = new List3CompBuilder().setName("sut").build();
    PortObserver<CardinalDirection> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<Integer> msg : i) {
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
        List.of(),
        List.of()
      ),
      // 2
      Arguments.of(
        List.of(msg(0)),
        List.of()
      ),
      // 3
      Arguments.of(
        List.of(msg(-1)),
        List.of()
      ),
      // 4
      Arguments.of(
        List.of(msg(1)),
        List.of()
      ),
      // 5
      Arguments.of(
        List.of(msg(Integer.MIN_VALUE)),
        List.of()
      ),
      // 6
      Arguments.of(
        List.of(msg(Integer.MAX_VALUE)),
        List.of()
      ),
      // 7
      Arguments.of(
        List.of(msg(-1), msg(0), msg(1)),
        List.of()
      )
    );
  }
}
