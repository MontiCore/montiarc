/* (c) https://github.com/MontiCore/monticore */
package foopackage;

import montiarc.rte.port.PortObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

public class FooCompTest {

  @Test
  public void shouldIncreaseInput() {
    // Given
    FooTestComp comp = new FooTestCompBuilder().setName("sut").build();
    PortObserver<Number> port_o = new PortObserver<>();
    comp.port_outPort().connect(port_o);

    // When
    comp.port_inPort().receive(msg(10));
    comp.port_inPort().receive(tk());
    comp.run();

    // Then
    Assertions.assertEquals(1, port_o.getObservedValues().size());
    Assertions.assertEquals(11, port_o.getObservedValues().get(0));
  }
}
