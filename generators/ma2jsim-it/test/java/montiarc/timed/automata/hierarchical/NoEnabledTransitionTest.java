/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class NoEnabledTransitionTest {

  @ParameterizedTest
  @ValueSource(strings = {"aa", "bbb", "ccc_c", "ddd_dd"})
  void testIO(@NotNull String stateToRemainIn) {
    Preconditions.checkNotNull(stateToRemainIn);

    List<Message<String>> input = inputForTest(stateToRemainIn);
    List<Message<String>> expected = expectedOutputsForTest(stateToRemainIn);

    // Given
    NoEnabledTransitionComp sut = new NoEnabledTransitionCompBuilder().setName("sut").build();
    PortObserver<String> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<String> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  private List<Message<String>> inputForTest(String stateToRemainIn) {
    return List.of(
      msg(stateToRemainIn), tk(),
      msg("check"), tk(),
      msg("This message should not trigger any transition"), tk(),
      msg("check"), tk()
    );
  }

  private List<Message<String>> expectedOutputsForTest(String stateToRemainIn) {
    return List.of(
      msg("-> " + stateToRemainIn), tk(),
      msg(stateToRemainIn), tk(),
      /* No message */ tk(),
      msg(stateToRemainIn), tk()
    );
  }
}
