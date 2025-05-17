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
class NoEnabledTransitionTest {
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<String>> input,
              @NotNull List<Message<String>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    NoEnabledTransitionComp sut = new NoEnabledTransitionCompBuilder().setName("sut").build();
    PortObserver<String> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    for (Message<String> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.runToCompletion();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // "aa"
      Arguments.of(
        List.of(msg("aa"), tk(), msg("no-trigger"), tk(), msg("no-trigger"), tk()),
        List.of(
          msg("INIT -> aa"), enter("a"), enter("aa"), doo("a"), doo("aa"), tk(),
          doo("a"), doo("aa"), tk(),
          doo("a"), doo("aa"), tk()
        )
      ),
      // "bbb"
      Arguments.of(
        List.of(msg("bbb"), tk(), msg("no-trigger"), tk(), msg("no-trigger"), tk()),
        List.of(
          msg("INIT -> bbb"), enter("b"), enter("bb"), enter("bbb"),
              doo("b"), doo("bb"), doo("bbb"), tk(),
          doo("b"), doo("bb"), doo("bbb"), tk(),
          doo("b"), doo("bb"), doo("bbb"), tk()
        )
      ),
      // "ccc_c"
      Arguments.of(
        List.of(msg("ccc_c"), tk(), msg("no-trigger"), tk(), msg("no-trigger"), tk()),
        List.of(
          msg("INIT -> ccc_c"), enter("c"), enter("cc"), enter("ccc"), enter("ccc_c"),
              doo("c"), doo("cc"), doo("ccc"), doo("ccc_c"), tk(),
          doo("c"), doo("cc"), doo("ccc"), doo("ccc_c"), tk(),
          doo("c"), doo("cc"), doo("ccc"), doo("ccc_c"), tk()
        )
      ),
      // "ddd_dd"
      Arguments.of(
        List.of(msg("ddd_dd"), tk(), msg("no-trigger"), tk(), msg("no-trigger"), tk()),
        List.of(
          msg("INIT -> ddd_dd"), enter("d"), enter("dd"), enter("ddd"), enter("ddd_d"), enter("ddd_dd"),
              doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), doo("ddd_dd"), tk(),
          doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), doo("ddd_dd"), tk(),
          doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), doo("ddd_dd"), tk()
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
}
