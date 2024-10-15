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
class NestedTransitionSourcesTest {

  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<String>> input,
              @NotNull List<Message<String>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    NestedTransitionSourcesComp sut = new NestedTransitionSourcesCompBuilder().setName("sut").build();
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
      Arguments.of(
        // a -> N
        List.of(msg("a"),    tk(), msg("a -> N"), tk(), msg("check"), tk()),
        List.of(msg("-> a"), tk(), msg("a -> N"), tk(), msg("Neutral"), tk())
      ),
      // b -> N
      Arguments.of(
        List.of(msg("b"),    tk(), msg("check"), tk(), msg("b -> N"), tk(), msg("check"), tk()),
        List.of(msg("-> b"), tk(), msg("bb"),    tk(), msg("b -> N"), tk(), msg("Neutral"), tk())
      ),
      // c -> N
      Arguments.of(
        List.of(msg("c"),    tk(), msg("check"), tk(), msg("c -> N"), tk(), msg("check"), tk()),
        List.of(msg("-> c"), tk(), msg("ccc"),   tk(), msg("c -> N"), tk(), msg("Neutral"), tk())
      ),
      // d -> N
      Arguments.of(
        List.of(msg("d"),    tk(), msg("check"), tk(), msg("d -> N"), tk(), msg("check"), tk()),
        List.of(msg("-> d"), tk(), msg("ddd_d"), tk(), msg("d -> N"), tk(), msg("Neutral"), tk())
      ),
      // bb -> N
      Arguments.of(
        List.of(msg("b"),    tk(), msg("check"), tk(), msg("bb -> N"), tk(), msg("check"), tk()),
        List.of(msg("-> b"), tk(), msg("bb"),    tk(), msg("bb -> N"), tk(), msg("Neutral"), tk())
      ),
      // ccc -> N
      Arguments.of(
        List.of(msg("c"),    tk(), msg("check"), tk(), msg("ccc -> N"), tk(), msg("check"), tk()),
        List.of(msg("-> c"), tk(), msg("ccc"),   tk(), msg("ccc -> N"), tk(), msg("Neutral"), tk())
      ),
      // ddd -> N
      Arguments.of(
        List.of(msg("d"),    tk(), msg("check"), tk(), msg("ddd -> N"), tk(), msg("check"), tk()),
        List.of(msg("-> d"), tk(), msg("ddd_d"), tk(), msg("ddd -> N"), tk(), msg("Neutral"), tk())
      ),
      // eee -> N
      Arguments.of(
        List.of(msg("e"),    tk(), msg("check"),  tk(), msg("eee -> N"), tk(), msg("check"), tk()),
        List.of(msg("-> e"), tk(), msg("eee_ee"), tk(), msg("eee -> N"), tk(), msg("Neutral"), tk())
      ),
      // fff -> N
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("fff -> N"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff -> N"), tk(), msg("Neutral"), tk())
      )
    );
  }
}
