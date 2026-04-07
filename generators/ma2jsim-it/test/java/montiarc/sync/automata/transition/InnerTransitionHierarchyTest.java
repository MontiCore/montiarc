/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.transition;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

public class InnerTransitionHierarchyTest {
  /**
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Integer>> input,
              @NotNull List<Message<String>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    InnerTransitionHierarchyComp sut = new InnerTransitionHierarchyCompBuilder().setName("sut").build();
    PortObserver<String> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    for (Message<Integer> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.runToCompletion();
    
    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(tk()),
        List.of(msg("Entry_S"), msg("Entry_A"), msg("Exit_A"), msg("Inner Trans 2"), msg("Entry_A"), tk())
      ),
      Arguments.of(
        List.of(msg(1), tk()),
        List.of(msg("Entry_S"), msg("Entry_A"), msg("Exit_A"), msg("Inner Trans 1"), msg("Entry_A"), tk())
      ),
      Arguments.of(
        List.of(msg(1), tk(), msg(1), tk()),
        List.of(msg("Entry_S"), msg("Entry_A"), msg("Exit_A"), msg("Inner Trans 1"), msg("Entry_A"), tk(), msg("Exit_A"), msg("Inner Trans 1"), msg("Entry_A"), tk())
      ),
      Arguments.of(
        List.of(msg(1), tk(), msg(1), tk(), msg(1), tk()),
        List.of(msg("Entry_S"), msg("Entry_A"), msg("Exit_A"), msg("Inner Trans 1"), msg("Entry_A"), tk(), msg("Exit_A"), msg("Inner Trans 1"), msg("Entry_A"), tk(), msg("Exit_A"), msg("Inner Trans 1"), msg("Entry_A"), tk())
      ),
      Arguments.of(
        List.of(msg(1), tk(), msg(0), tk()),
        List.of(msg("Entry_S"), msg("Entry_A"), msg("Exit_A"), msg("Inner Trans 1"), msg("Entry_A"), tk(), msg("Exit_A"), msg("Inner Trans 2"), msg("Entry_A"), tk())
      ),
      Arguments.of(
        List.of(msg(2), tk()),
        List.of(msg("Entry_S"), msg("Entry_A"), msg("Exit_A"), msg("Trans_A_B"), msg("Entry_B"), tk())
      )
    );
  }
}
