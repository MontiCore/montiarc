/* (c) https://github.com/MontiCore/monticore */
package montiarc.untimed;

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
class AtomicComponentTest {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<OnOff>> input,
              @NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    AtomicComponentComp sut = new AtomicComponentCompBuilder().setName("sut").build();
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
        List.of(msg(ON)),
        List.of()
      ),
      Arguments.of(
        List.of(msg(ON), tk()),
        List.of(tk())
      ),
      Arguments.of(
        List.of(msg(ON), tk(), msg(OFF), tk(), msg(ON), msg(OFF)),
        List.of(         tk(),           tk()                   )
      )
    );
  }
}
