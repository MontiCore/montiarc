/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.structural;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class UnionForwardTest {

  @Test
  void shouldWidenToUnion() {
    // Given
    UnionForwardComp sut = new UnionForwardCompBuilder().setName("sut").build();
    // union types are generated as java.lang.Object
    PortObserver<Object> widened = new PortObserver<>();
    sut.port_widened().connect(widened);

    // When
    sut.port_member().receive(Message.of(42));
    sut.port_member().receive(tk());
    sut.port_member().receive(Message.of(-1));
    sut.port_member().receive(tk());
    sut.runToCompletion();

    // Then
    Assertions.assertThat(widened.getObservedValues()).containsExactly(42, -1);
  }
}
