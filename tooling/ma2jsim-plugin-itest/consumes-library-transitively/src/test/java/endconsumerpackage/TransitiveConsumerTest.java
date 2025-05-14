/* (c) https://github.com/MontiCore/monticore */
package endconsumerpackage;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class TransitiveConsumerTest {

  @Test
  void checkComponentIsUsable() {
    // Given
    TransitiveConsumerComp comp = new TransitiveConsumerCompBuilder().setName("sut").build();
    PortObserver<Number> port_o = new PortObserver<>();
    comp.port_outgoing().connect(port_o);

    // When
    comp.port_incoming().receive(msg(10));
    comp.port_incoming().receive(tk());
    comp.run();

    // Then
    Assertions.assertEquals(1, port_o.getObservedValues().size());
    Assertions.assertEquals(11, port_o.getObservedValues().get(0));
  }
}
