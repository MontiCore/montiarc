/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical.actions;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class InitialAndEntryActionsInHierarchyTest {

  @Test
  void testIO() {
    // Given
    InitialAndEntryActionsInHierarchyComp sut = new InitialAndEntryActionsInHierarchyCompBuilder().setName("sut").build();
    PortObserver<String> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();
    sut.port_i().receive(tk());
    sut.port_i().receive(tk());
    sut.port_i().receive(msg("trigger"));
    sut.port_i().receive(msg("trigger"));
    sut.run();

    // Then
    List<Message<String>> expectedOutput = List.of(
      msg("INIT A"),
      msg("INIT AA"),
      msg("-> A"),
      msg("-> AA"),
      msg("A -> B"),
      tk(),
      msg("B -> A"),
      msg("-> A"),
      msg("-> AA"),
      tk(),
      // processing triggering messages
      msg("A -> B"),
      msg("B -> A"),
      msg("-> A"),
      msg("-> AA")
    );
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expectedOutput);
  }
}
