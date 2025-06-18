/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.composition;

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
public class SourceEncapsulationTest {

  @ParameterizedTest
  @MethodSource("io")
  void testIOToCompletion(int executionLength,
                          @NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    SourceEncapsulationComp sut = new SourceEncapsulationCompBuilder().setName("sut").build();
    PortObserver<OnOff> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    for (int i = 0; i < executionLength; i++) {
      sut.port_i().receive(tk());
    }

    sut.runToCompletion();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  @ParameterizedTest
  @MethodSource("io")
  void testIORun(int executionLength,
                 @NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    SourceEncapsulationComp sut = new SourceEncapsulationCompBuilder().setName("sut").build();
    PortObserver<OnOff> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);
    sut.run(executionLength);

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        1,
        List.of(msg(OFF), tk())
      ),
      Arguments.of(
        2,
        List.of(msg(OFF), tk(), msg(ON), tk())
      ),
      Arguments.of(
        3,
        List.of(msg(OFF), tk(), msg(ON), tk(), msg(OFF), tk())
      ),
      Arguments.of(
        4,
        List.of(msg(OFF), tk(), msg(ON), tk(), msg(OFF), tk(), msg(ON), tk())
      ),
      Arguments.of(
        5,
        List.of(msg(OFF), tk(), msg(ON), tk(), msg(OFF), tk(), msg(ON), tk(), msg(OFF), tk())
      ),
      Arguments.of(
        6,
        List.of(msg(OFF), tk(), msg(ON), tk(), msg(OFF), tk(), msg(ON), tk(), msg(OFF), tk(), msg(ON), tk())
      )
    );
  }
}
