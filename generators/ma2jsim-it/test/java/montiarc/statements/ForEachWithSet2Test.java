/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Test;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.types.Signal.SIGNAL;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class ForEachWithSet2Test {

  @Test
  void testIO() {
    // Given
    ForEachWithSet2Comp sut = new ForEachWithSet2CompBuilder().setName("sut").build();

    PortObserver<Integer> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.port_i().receive(msg(SIGNAL));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(0), msg(1));
  }
}
