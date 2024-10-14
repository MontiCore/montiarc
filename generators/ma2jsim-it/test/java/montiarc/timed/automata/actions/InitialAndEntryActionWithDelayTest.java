/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.actions;

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
class InitialAndEntryActionWithDelayTest {

  /**
   * @param input the input stream on port i
   * @param expected  the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<String>> input,
              @NotNull List<Message<String>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    InitialAndEntryActionWithDelayComp sut = new InitialAndEntryActionWithDelayCompBuilder().setName("sut").build();
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
        List.of(msg("trigger"), tk(), msg("trigger"), tk()),
        List.of(
          msg("INIT"), tk(),
          msg("Enter A"), msg("A -> B"), msg("Enter B"), msg("Do B"), tk(),
          msg("B -> A"), msg("Enter A"), msg("Do A"), tk()
        )
      ),
      Arguments.of(
        List.of(msg("noop"), tk(), msg("trigger"), tk()),
        List.of(
          msg("INIT"), tk(),
          msg("Enter A"), msg("Do A"), tk(),
          msg("A -> B"), msg("Enter B"), msg("Do B"), tk()
        )
      )
    );
  }
}
