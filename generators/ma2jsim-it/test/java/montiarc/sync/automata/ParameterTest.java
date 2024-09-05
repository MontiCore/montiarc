/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

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
class ParameterTest {

  /**
   * @param parameter the parameter p of the component
   * @param expected  the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull OnOff parameter,
              @NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(parameter);
    Preconditions.checkNotNull(expected);

    // Given
    ParameterComp sut = new ParameterCompBuilder().setName("sut").set_param_p(parameter).build();
    PortObserver<OnOff> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();
    sut.run(1);

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        OnOff.OFF,
        List.of(msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        OnOff.ON,
        List.of(msg(OnOff.ON), tk())
      )
    );
  }
}
