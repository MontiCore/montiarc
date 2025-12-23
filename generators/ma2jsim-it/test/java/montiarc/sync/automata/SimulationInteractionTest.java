/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.lang.Duration;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
public class SimulationInteractionTest {

  @Test
  void testIO() {
    // Given
    SimulationInteractionComp sut = new SimulationInteractionCompBuilder().setName("sut").build();
    PortObserver<Duration> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.run(10, 1000000);

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(List.of(msg(Duration.ofMilliseconds(1)), tk()));
  }
}
