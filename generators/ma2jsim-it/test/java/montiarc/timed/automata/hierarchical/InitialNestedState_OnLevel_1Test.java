/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class InitialNestedState_OnLevel_1Test {

  @Test
  void testIO() {
    // Given
    InitialNestedState_OnLevel_1Comp sut = new InitialNestedState_OnLevel_1CompBuilder().setName("sut").build();
    PortObserver<String> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();
    sut.run(1);

    // Then
    List<Message<String>> expectedOutput = List.of(msg("AA"), tk());
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expectedOutput);
  }
}
