/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class ConstTimerTest {

  @ParameterizedTest
  @MethodSource("absIntegerTestProvider")
  void test(int duration) {
    // Given
    ConstTimerComp sut = new ConstTimerCompBuilder().setName("sut").set_param_duration(Duration.ofSeconds(duration)).build();
    PortObserver<Signal> port_completed = new PortObserver<>();

    sut.port_completed().connect(port_completed);
    List<Message<Signal>> expected = new ArrayList<>();

    // When
    sut.port_start().receive(tk());
    sut.port_start().receive(msg(Signal.get()));
    for (int i = 0 ; i < duration ; i++) {
      sut.port_start().receive(tk());
      expected.add(tk());
    }

    sut.runToCompletion(1000000000L);

    // Then
    expected.add(msg(Signal.get()));
    expected.add(tk());
    assertThat(port_completed.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> absIntegerTestProvider() {
    return Stream.of(
      Arguments.of(1),
      Arguments.of(2),
      Arguments.of(3),
      Arguments.of(4)
    );
  }
}
