/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.types.OnOff;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

class ParameterForwardTest {

  /**
   * @param parameter the parameter p of the component
   * @param expected  the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@Nullable OnOff parameter,
              @NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    ParameterForwardCompBuilder builder = new ParameterForwardCompBuilder().setName("sut");
    if (parameter != null) {
      builder.set_param_param(parameter);
    }
    ParameterForwardComp sut = builder.build();
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
        null,
        List.of(msg(OnOff.OFF), tk())
      ),
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
