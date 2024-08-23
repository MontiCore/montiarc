/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.composition;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
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

class ChangingUseOfOutPortsTest {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<OnOff>> input,
              @NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    ChangingUseOfOutPortsComp sut = new ChangingUseOfOutPortsCompBuilder().setName("sut").build();
    PortObserver<OnOff> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    input.forEach(sut.port_i::receive);

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
        List.of(msg(ON), tk()),
        List.of(msg(ON), tk())
      ),
      Arguments.of(
        List.of(msg(ON), tk(), msg(OFF), tk(), msg(ON), tk()),
        List.of(msg(ON), tk(),           tk(), msg(ON), tk())
      )
    );
  }
}
