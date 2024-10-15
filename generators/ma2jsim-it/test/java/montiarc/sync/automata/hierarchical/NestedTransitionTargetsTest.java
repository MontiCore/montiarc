/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class NestedTransitionTargetsTest {

  /**
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<String>> input,
              @NotNull List<Message<String>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    NestedTransitionTargetsComp sut = new NestedTransitionTargetsCompBuilder().setName("sut").build();
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

  static Stream<Arguments> io() {
    return Stream.of(
      // Neutral -> a
      Arguments.of(
        List.of(msg("Neutral"),    tk(), msg("N -> a"), tk(), msg("check"), tk()),
        List.of(msg("-> Neutral"), tk(), msg("N -> a"), tk(), msg("a"), tk())
      ),
      // Neutral -> b
      Arguments.of(
        List.of(msg("Neutral"),    tk(), msg("N -> b"), tk(), msg("check"), tk()),
        List.of(msg("-> Neutral"), tk(), msg("N -> b"), tk(), msg("bb"), tk())
      ),
      // Neutral -> c
      Arguments.of(
        List.of(msg("Neutral"),    tk(), msg("N -> c"), tk(), msg("check"), tk()),
        List.of(msg("-> Neutral"), tk(), msg("N -> c"), tk(), msg("ccc"), tk())
      ),
      // Neutral -> d
      Arguments.of(
        List.of(msg("Neutral"),    tk(), msg("N -> d"), tk(), msg("check"), tk()),
        List.of(msg("-> Neutral"), tk(), msg("N -> d"), tk(), msg("ddd_d"), tk())
      ),
      // Neutral -> bb
      Arguments.of(
        List.of(msg("Neutral"),    tk(), msg("N -> bb"), tk(), msg("check"), tk()),
        List.of(msg("-> Neutral"), tk(), msg("N -> bb"), tk(), msg("bb"), tk())
      ),
      // Neutral -> ccc
      Arguments.of(
        List.of(msg("Neutral"),    tk(), msg("N -> ccc"), tk(), msg("check"), tk()),
        List.of(msg("-> Neutral"), tk(), msg("N -> ccc"), tk(), msg("ccc"), tk())
      ),
      // Neutral -> ddd
      Arguments.of(
        List.of(msg("Neutral"),    tk(), msg("N -> ddd"), tk(), msg("check"), tk()),
        List.of(msg("-> Neutral"), tk(), msg("N -> ddd"), tk(), msg("ddd_d"), tk())
      ),
      // Neutral -> eee
      Arguments.of(
        List.of(msg("Neutral"),    tk(), msg("N -> eee"), tk(), msg("check"), tk()),
        List.of(msg("-> Neutral"), tk(), msg("N -> eee"), tk(), msg("eee_ee"), tk())
      ),
      // Neutral -> fff
      Arguments.of(
        List.of(msg("Neutral"),    tk(), msg("N -> fff"), tk(), msg("check"), tk()),
        List.of(msg("-> Neutral"), tk(), msg("N -> fff"), tk(), msg("fff_fff"), tk())
      )
    );
  }
}
