/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
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

@JSimTest
class BoxedDefaultParameterTest {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(@Nullable Double parameter,
              @NotNull List<Message<Double>> expectedDouble,
              @NotNull List<Message<Number>> expectedNumber) {
    // Given
    BoxedDefaultParameterCompBuilder builder = new BoxedDefaultParameterCompBuilder()
      .setName("sut");
    if (parameter != null) {
      builder.set_param_param(parameter);
    }
    BoxedDefaultParameterComp sut = builder.build();
    PortObserver<Double> port_o = new PortObserver<>();
    PortObserver<Number> port_n = new PortObserver<>();

    sut.port_o().connect(port_o);
    sut.port_n().connect(port_n);

    // When
    sut.run(2);

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expectedDouble);
    Assertions.assertThat(port_n.getObservedMessages()).containsExactlyElementsOf(expectedNumber);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        null,
        List.of(msg(5.0), tk(), msg(5.0), tk()),
        List.of(msg((Number) 5), tk(), msg((Number) 5), tk())
      ),
      Arguments.of(
        2.5,
        List.of(msg(2.5), tk(), msg(2.5), tk()),
        List.of(msg((Number) 5), tk(), msg((Number) 5), tk())
      )
    );
  }
}
