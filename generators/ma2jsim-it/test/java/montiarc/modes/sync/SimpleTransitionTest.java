/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync;

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
import static montiarc.types.OnOff.OFF;
import static montiarc.types.OnOff.ON;

@JSimTest
class SimpleTransitionTest {

  /**
   * @param input    the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<OnOff>> input,
              @NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    SimpleTransitionComp sut = new SimpleTransitionCompBuilder().setName("sut").build();
    PortObserver<OnOff> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<OnOff> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(),
        List.of()
      ),
      Arguments.of(
        List.of(tk()),
        List.of(tk())
      ),
      Arguments.of(
        List.of(tk(), tk()),
        List.of(tk(), tk())
      ),
      Arguments.of(
        List.of(msg(OFF)),
        List.of()
      ),
      // Before the sub component can analyze anything, it is replaced by the mode switch.
      // Consequently, the inverter processes the message and sends an ON as a reaction to OFF.
      Arguments.of(
        List.of(msg(OFF), tk()),
        List.of(msg(ON), tk())
      )
    );
  }
}
