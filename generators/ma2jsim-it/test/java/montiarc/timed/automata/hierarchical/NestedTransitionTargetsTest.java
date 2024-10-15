/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical;

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
        List.of(msg("Neutral"),    msg("N -> a"), msg("check")),
        List.of(msg("-> Neutral"), msg("N -> a"), msg("a"))
      ),
      // Neutral -> b
      Arguments.of(
        List.of(msg("Neutral"),    msg("N -> b"), msg("check")),
        List.of(msg("-> Neutral"), msg("N -> b"), msg("bb"))
      ),
      // Neutral -> c
      Arguments.of(
        List.of(msg("Neutral"),    msg("N -> c"), msg("check")),
        List.of(msg("-> Neutral"), msg("N -> c"), msg("ccc"))
      ),
      // Neutral -> d
      Arguments.of(
        List.of(msg("Neutral"),    msg("N -> d"), msg("check")),
        List.of(msg("-> Neutral"), msg("N -> d"), msg("ddd_d"))
      ),
      // Neutral -> bb
      Arguments.of(
        List.of(msg("Neutral"),    msg("N -> bb"), msg("check")),
        List.of(msg("-> Neutral"), msg("N -> bb"), msg("bb"))
      ),
      // Neutral -> ccc
      Arguments.of(
        List.of(msg("Neutral"),    msg("N -> ccc"), msg("check")),
        List.of(msg("-> Neutral"), msg("N -> ccc"), msg("ccc"))
      ),
      // Neutral -> ddd
      Arguments.of(
        List.of(msg("Neutral"),    msg("N -> ddd"), msg("check")),
        List.of(msg("-> Neutral"), msg("N -> ddd"), msg("ddd_d"))
      ),
      // Neutral -> eee
      Arguments.of(
        List.of(msg("Neutral"),    msg("N -> eee"), msg("check")),
        List.of(msg("-> Neutral"), msg("N -> eee"), msg("eee_ee"))
      ),
      // Neutral -> fff
      Arguments.of(
        List.of(msg("Neutral"),    msg("N -> fff"), msg("check")),
        List.of(msg("-> Neutral"), msg("N -> fff"), msg("fff_fff"))
      )
    );
  }
}
