/* (c) https://github.com/MontiCore/monticore */
package montiarc.oracle;

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
import static montiarc.types.OnOff.ON;

@JSimTest
class CompositionLeastUsedStereotypeTest {

  static Stream<Arguments> inputExpectedOutputAndOracleProvider() {
    return Stream.of(
      Arguments.of(
        List.of(msg(ON), tk(), msg(ON), tk(), msg(ON), tk(), msg(ON), tk()),
        List.of(msg(1), tk(), msg(1), tk(), msg(1), tk(), msg(1), tk())
      )
    );
  }

  /**
   * @param input    the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("inputExpectedOutputAndOracleProvider")
  void testIO(@NotNull List<Message<OnOff>> input,
              @NotNull List<Message<Number>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    CompositionLeastUsedStereotypeComp sut = new CompositionLeastUsedStereotypeCompBuilder()
      .setName("sut")
      .build();

    PortObserver<Number> port_o = new PortObserver<>();
    sut.port_o().connect(port_o);

    // When
    for (Message<OnOff> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.runToCompletion();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }
}
