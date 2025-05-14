/* (c) https://github.com/MontiCore/monticore */
package montiarc.mixed_timing.composition;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.OnOff;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
public class FeedbackLoopTest {

  /**
   * @param ticks    the number of ticks the system should be executed
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull Integer ticks,
              @NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(ticks);
    Preconditions.checkNotNull(expected);

    // Given
    FeedbackLoopComp sut = new FeedbackLoopCompBuilder().setName("sut").build();
    PortObserver<OnOff> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.run(ticks);

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        0,
        List.of(msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        1,
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        2,
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk())
      )
    );
  }
}
