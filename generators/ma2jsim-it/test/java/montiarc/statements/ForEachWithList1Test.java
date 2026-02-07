/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static montiarc.rte.msg.MessageFactory.msg;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class ForEachWithList1Test {

  @Test
  void testIO() {
    // Given
    ForEachWithList1Comp sut = new ForEachWithList1CompBuilder().setName("sut").build();

    PortObserver<Integer> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.port_i().receive(msg(List.of(0, 1)));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(0), msg(1));
  }
}
