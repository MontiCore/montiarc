/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;

@JSimTest
public class NumberListTest {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Integer>> i,
              @NotNull List<Message<Number>> o) {
    Preconditions.checkNotNull(i);
    Preconditions.checkNotNull(o);

    // Given
    NumberListComp sut = new NumberListCompBuilder().setName("sut").build();
    PortObserver<Number> port_o = new PortObserver<>();

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
      // reads all mixed numeric literals in declaration order
      Arguments.of(
        List.of(msg(0), msg(1), msg(2)),
        List.of(msg((Number) 1.0F), msg((Number) 3.33333D), msg((Number) 1.6F))
      ),
      // ignores indexes outside the list bounds
      Arguments.of(
        List.of(msg(-1), msg(3), msg(Integer.MAX_VALUE)),
        List.of()
      )
    );
  }
}
