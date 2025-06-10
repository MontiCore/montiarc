/* (c) https://github.com/MontiCore/monticore */
package montiarc.oracle;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.oracle.OracleFactory;
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
import static montiarc.rte.oracle.OracleFactory.preferFirst;
import static montiarc.rte.oracle.OracleFactory.preferLast;
import static montiarc.types.OnOff.ON;

@JSimTest
class StatechartInSubCompOfModeTest {

  /**
   * @param input    the input stream on port i
   * @param expected the expected output stream on port o
   * @param oracleFactory the oracle factory to use
   */
  @ParameterizedTest
  @MethodSource("inputExpectedOutputAndOracleProvider")
  void testIO(@NotNull List<Message<OnOff>> input,
              @NotNull List<Message<Number>> expected,
              @NotNull OracleFactory oracleFactory) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);
    Preconditions.checkNotNull(oracleFactory);

    // Given
    StatechartInSubCompOfModeComp sut =
      new StatechartInSubCompOfModeCompBuilder()
        .setName("sut")
        .setOracleFactory(oracleFactory)
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

  static Stream<Arguments> inputExpectedOutputAndOracleProvider() {
    return Stream.of(
      Arguments.of(
        List.of(tk(), tk(), msg(ON), tk(), tk(), msg(ON), tk()),
        List.of(tk(), tk(), msg(2), tk(), tk(), msg(2), tk()),
        OracleFactory.withDefaultStrategy(preferFirst())
          .set4Comp("sut.S1.sub", preferLast())
      )
    );
  }
}
