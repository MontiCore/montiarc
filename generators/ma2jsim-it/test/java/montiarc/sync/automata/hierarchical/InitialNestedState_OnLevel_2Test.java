/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class InitialNestedState_OnLevel_2Test {

  @Test
  void testIO() {
    // Given
    InitialNestedState_OnLevel_2Comp sut = new InitialNestedState_OnLevel_2CompBuilder().setName("sut").build();
    PortObserver<String> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();
    sut.run(1);

    // Then
    List<Message<String>> expectedOutput = List.of(msg("AAA"), tk());
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expectedOutput);
  }
}
