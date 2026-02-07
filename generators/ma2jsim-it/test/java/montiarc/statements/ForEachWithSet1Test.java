/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static montiarc.rte.msg.MessageFactory.msg;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class ForEachWithSet1Test {

  @Test
  void testIO() {
    // Given
    ForEachWithSet1Comp sut = new ForEachWithSet1CompBuilder().setName("sut").build();

    PortObserver<Integer> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    Set<Integer> input = new LinkedHashSet<>(2);
    input.add(0);
    input.add(1);

    // When
    sut.port_i().receive(msg(input));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(0), msg(1));
  }
}
