/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical.actions;

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
    for (Message<String> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // N -> a
      Arguments.of(
        List.of(msg("N"), tk(), msg("N -> a"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"), doo("N"), tk(),
          exit("N"), msg("N -> a"),
              enter("a"), doo("a"), tk()
        )
      ),
      // N -> a  without intermediate tick
      Arguments.of(
        List.of(msg("N"), msg("N -> a"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"),
          exit("N"), msg("N -> a"),
          enter("a"), doo("a"), tk()
        )
      ),
      // N -> b
      Arguments.of(
        List.of(msg("N"), tk(), msg("N -> b"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"), doo("N"), tk(),
          exit("N"), msg("N -> b"),
              enter("b"), enter("bb"), doo("b"), doo("bb"), tk()
        )
      ),
      // N -> b without intermediate tick
      Arguments.of(
        List.of(msg("N"), msg("N -> b"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"),
          exit("N"), msg("N -> b"),
          enter("b"), enter("bb"), doo("b"), doo("bb"), tk()
        )
      ),
      // N -> c
      Arguments.of(
        List.of(msg("N"), tk(), msg("N -> c"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"), doo("N"), tk(),
          exit("N"), msg("N -> c"),
              enter("c"), enter("cc"), enter("ccc"), doo("c"),
              doo("cc"), doo("ccc"), tk()
        )
      ),
      // N -> c without intermediate tick
      Arguments.of(
        List.of(msg("N"), msg("N -> c"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"),
          exit("N"), msg("N -> c"),
          enter("c"), enter("cc"), enter("ccc"), doo("c"),
          doo("cc"), doo("ccc"), tk()
        )
      ),
      // N -> d
      Arguments.of(
        List.of(msg("N"), tk(), msg("N -> d"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"), doo("N"), tk(),
          exit("N"), msg("N -> d"),
              enter("d"), enter("dd"), enter("ddd"), enter("ddd_d"),
              doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), tk()
        )
      ),
      // N -> d without intermediate tick
      Arguments.of(
        List.of(msg("N"), msg("N -> d"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"),
          exit("N"), msg("N -> d"),
          enter("d"), enter("dd"), enter("ddd"), enter("ddd_d"),
          doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), tk()
        )
      ),
      // N -> bb
      Arguments.of(
        List.of(msg("N"), tk(), msg("N -> bb"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"), doo("N"), tk(),
          exit("N"), msg("N -> bb"),
              enter("b"), enter("bb"), doo("b"), doo("bb"), tk()
        )
      ),
      // N -> bb without intermediate tick
      Arguments.of(
        List.of(msg("N"), msg("N -> bb"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"),
          exit("N"), msg("N -> bb"),
          enter("b"), enter("bb"), doo("b"), doo("bb"), tk()
        )
      ),
      // N -> ccc
      Arguments.of(
        List.of(msg("N"), tk(), msg("N -> ccc"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"), doo("N"), tk(),
          exit("N"), msg("N -> ccc"),
              enter("c"), enter("cc"), enter("ccc"), doo("c"),
              doo("cc"), doo("ccc"), tk()
        )
      ),
      // N -> ccc without intermediate tick
      Arguments.of(
        List.of(msg("N"), msg("N -> ccc"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"),
          exit("N"), msg("N -> ccc"),
          enter("c"), enter("cc"), enter("ccc"), doo("c"),
          doo("cc"), doo("ccc"), tk()
        )
      ),
      // N -> ddd
      Arguments.of(
        List.of(msg("N"), tk(), msg("N -> ddd"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"), doo("N"), tk(),
          exit("N"), msg("N -> ddd"),
              enter("d"), enter("dd"), enter("ddd"), enter("ddd_d"),
              doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), tk()
        )
      ),
      // N -> ddd without intermediate tick
      Arguments.of(
        List.of(msg("N"), msg("N -> ddd"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"),
          exit("N"), msg("N -> ddd"),
          enter("d"), enter("dd"), enter("ddd"), enter("ddd_d"),
          doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), tk()
        )
      ),
      // N -> eee
      Arguments.of(
        List.of(msg("N"), tk(), msg("N -> eee"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"), doo("N"), tk(),
          exit("N"), msg("N -> eee"),
              enter("e"), enter("ee"), enter("eee"), enter("eee_e"), enter("eee_ee"),
              doo("e"), doo("ee"), doo("eee"), doo("eee_e"), doo("eee_ee"), tk()
        )
      ),
      // N -> eee without intermediate tick
      Arguments.of(
        List.of(msg("N"), msg("N -> eee"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"),
          exit("N"), msg("N -> eee"),
          enter("e"), enter("ee"), enter("eee"), enter("eee_e"), enter("eee_ee"),
          doo("e"), doo("ee"), doo("eee"), doo("eee_e"), doo("eee_ee"), tk()
        )
      ),
      // N -> fff
      Arguments.of(
        List.of(msg("N"), tk(), msg("N -> fff"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"), doo("N"), tk(),
          exit("N"), msg("N -> fff"),
              enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
              doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk()
        )
      ),
      // N -> fff without intermediate tick
      Arguments.of(
        List.of(msg("N"), msg("N -> fff"), tk()),
        List.of(
          msg("INIT -> N"), enter("N"),
          exit("N"), msg("N -> fff"),
          enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
          doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk()
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
