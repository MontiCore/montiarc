/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.OnOff;
import org.junit.jupiter.api.Test;

import java.util.List;

import static montiarc.rte.msg.MessageFactory.msg;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class ForEachWithListTest {

  @Test
  void testIO() {
    // Given
    ForEachWithListComp sut = new ForEachWithListCompBuilder().setName("sut").build();

    PortObserver<OnOff> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.port_i().receive(msg(List.of(OnOff.ON, OnOff.OFF)));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(OnOff.ON), msg(OnOff.OFF));
  }
}
