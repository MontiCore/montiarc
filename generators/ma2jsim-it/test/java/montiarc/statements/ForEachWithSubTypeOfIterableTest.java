/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.ItInt;
import org.junit.jupiter.api.Test;

import static montiarc.rte.msg.MessageFactory.msg;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class ForEachWithSubTypeOfIterableTest {

  @Test
  void testIO() {
    // Given
    ForEachWithSubTypeOfIterableComp sut = new ForEachWithSubTypeOfIterableCompBuilder().setName("sut").build();

    PortObserver<Integer> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.port_i().receive(msg(new ItInt(0, 1)));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(0), msg(1));
  }
}
