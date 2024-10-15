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
        List.of(msg("a"),    msg("a -> N"), msg("check")),
        List.of(msg("-> a"), msg("a -> N"), msg("Neutral"))
      ),
      // b -> N
      Arguments.of(
        List.of(msg("b"),    msg("check"), msg("b -> N"), msg("check")),
        List.of(msg("-> b"), msg("bb"),    msg("b -> N"), msg("Neutral"))
      ),
      // c -> N
      Arguments.of(
        List.of(msg("c"),    msg("check"), msg("c -> N"), msg("check")),
        List.of(msg("-> c"), msg("ccc"),   msg("c -> N"), msg("Neutral"))
      ),
      // d -> N
      Arguments.of(
        List.of(msg("d"),    msg("check"), msg("d -> N"), msg("check")),
        List.of(msg("-> d"), msg("ddd_d"), msg("d -> N"), msg("Neutral"))
      ),
      // bb -> N
      Arguments.of(
        List.of(msg("b"),    msg("check"), msg("bb -> N"), msg("check")),
        List.of(msg("-> b"), msg("bb"),    msg("bb -> N"), msg("Neutral"))
      ),
      // ccc -> N
      Arguments.of(
        List.of(msg("c"),    msg("check"), msg("ccc -> N"), msg("check")),
        List.of(msg("-> c"), msg("ccc"),   msg("ccc -> N"), msg("Neutral"))
      ),
      // ddd -> N
      Arguments.of(
        List.of(msg("d"),    msg("check"), msg("ddd -> N"), msg("check")),
        List.of(msg("-> d"), msg("ddd_d"), msg("ddd -> N"), msg("Neutral"))
      ),
      // eee -> N
      Arguments.of(
        List.of(msg("e"),    msg("check"),  msg("eee -> N"), msg("check")),
        List.of(msg("-> e"), msg("eee_ee"), msg("eee -> N"), msg("Neutral"))
      ),
      // fff -> N
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("fff -> N"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff -> N"), msg("Neutral"))
      )
    );
  }
}
