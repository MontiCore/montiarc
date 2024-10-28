/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical.actions;

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
      // a -> N
      Arguments.of(
        List.of(msg("a"), tk(), msg("a -> N"), tk()),
        List.of(
          msg("INIT -> a"), enter("a"), doo("a"), tk(),
          exit("a"), msg("a -> N"), enter("N"), doo("N"), tk()
        )
      ),
      // b -> N
      Arguments.of(
        List.of(msg("b"), tk(), msg("b -> N"), tk()),
        List.of(
          msg("INIT -> b"), enter("b"), enter("bb"), doo("b"), doo("bb"), tk(),
          exit("bb"), exit("b"), msg("b -> N"), enter("N"), doo("N"), tk()
        )
      ),
      // c -> N
      Arguments.of(
        List.of(msg("c"), tk(), msg("c -> N"), tk()),
        List.of(
          msg("INIT -> c"), enter("c"), enter("cc"), enter("ccc"),
              doo("c"), doo("cc"), doo("ccc"), tk(),
          exit("ccc"), exit("cc"), exit("c"),
              msg("c -> N"), enter("N"), doo("N"), tk()
        )
      ),
      // d -> N
      Arguments.of(
        List.of(msg("d"), tk(), msg("d -> N"), tk()),
        List.of(
          msg("INIT -> d"), enter("d"), enter("dd"), enter("ddd"), enter("ddd_d"),
              doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), tk(),
          exit("ddd_d"), exit("ddd"), exit("dd"), exit("d"),
              msg("d -> N"), enter("N"), doo("N"), tk()
        )
      ),
      // bb -> N
      Arguments.of(
        List.of(msg("b"), tk(), msg("bb -> N"), tk()),
        List.of(
          msg("INIT -> b"), enter("b"), enter("bb"), doo("b"), doo("bb"), tk(),
          exit("bb"), exit("b"), msg("bb -> N"), enter("N"), doo("N"), tk()
        )
      ),
      // ccc -> N
      Arguments.of(
        List.of(msg("c"), tk(), msg("ccc -> N"), tk()),
        List.of(
          msg("INIT -> c"), enter("c"), enter("cc"), enter("ccc"),
              doo("c"), doo("cc"), doo("ccc"), tk(),
          exit("ccc"), exit("cc"), exit("c"),
              msg("ccc -> N"), enter("N"), doo("N"), tk()
        )
      ),
      // ddd -> N
      Arguments.of(
        List.of(msg("d"), tk(), msg("ddd -> N"), tk()),
        List.of(
          msg("INIT -> d"), enter("d"), enter("dd"), enter("ddd"), enter("ddd_d"),
              doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), tk(),
          exit("ddd_d"), exit("ddd"), exit("dd"), exit("d"),
              msg("ddd -> N"), enter("N"), doo("N"), tk()
        )
      ),
      // eee -> N
      Arguments.of(
        List.of(msg("e"), tk(), msg("eee -> N"), tk()),
        List.of(
          msg("INIT -> e"), enter("e"), enter("ee"), enter("eee"), enter("eee_e"), enter("eee_ee"),
              doo("e"), doo("ee"), doo("eee"), doo("eee_e"), doo("eee_ee"), tk(),
          exit("eee_ee"), exit("eee_e"), exit("eee"), exit("ee"), exit("e"),
              msg("eee -> N"), enter("N"), doo("N"), tk()
        )
      ),
      // fff -> N
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff -> N"), tk()),
        List.of(
          msg("INIT -> f"), enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
              doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"), exit("fff_f"), exit("fff"), exit("ff"), exit("f"),
              msg("fff -> N"), enter("N"), doo("N"), tk()
        )
      )
    );
  }

  private static Message<String> enter(String stateName) {
    return msg("-> " + stateName);
  }

  private static Message<String> doo(String stateName) {
    return msg("~ " + stateName);
  }

  private static Message<String> exit(String stateName) {
    return msg(stateName + " ->");
  }
}
