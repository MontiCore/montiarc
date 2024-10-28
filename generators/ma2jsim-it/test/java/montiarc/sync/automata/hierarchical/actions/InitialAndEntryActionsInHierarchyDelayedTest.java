/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical.actions;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class InitialAndEntryActionsInHierarchyDelayedTest {

  @Test
  void testIO() {
    // Given
    InitialAndEntryActionsInHierarchyDelayedComp sut =
      new InitialAndEntryActionsInHierarchyDelayedCompBuilder().setName("sut").build();
    PortObserver<String> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();
    sut.run(2);

    // Then
    List<Message<String>> expectedOutput = List.of(
      msg("INIT A"),
      msg("INIT AA"),
      tk(),
      msg("-> A"),
      msg("-> AA"),
      msg("A -> B"),
      tk(),
      msg("B -> A"),
      msg("-> A"),
      msg("-> AA"),
      tk()
    );
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expectedOutput);
  }
}
