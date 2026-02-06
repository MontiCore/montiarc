/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.OnOff;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static montiarc.rte.msg.MessageFactory.msg;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class ForEachWithSetTest {

  @Test
  void testIO() {
    // Given
    ForEachWithSetComp sut = new ForEachWithSetCompBuilder().setName("sut").build();

    PortObserver<OnOff> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    Set<OnOff> input = new LinkedHashSet<>(2);
    input.add(OnOff.ON);
    input.add(OnOff.OFF);

    // When
    sut.port_i().receive(msg(input));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(OnOff.ON), msg(OnOff.OFF));
  }
}
