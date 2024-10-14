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
class EntryExitDoInternalTest {

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
    EntryExitDoInternalComp sut = new EntryExitDoInternalCompBuilder().setName("sut").build();
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
        List.of(msg("noop"), tk(), tk(), tk()),
        List.of(msg("Enter A"),                              // <- Initial state entered
                msg("Do A"), tk(),                           // <- Action of first time frame
                msg("Do A"), tk(),  // <- Action of second time frame
                msg("Do A"), tk())                           // <- ...
      ),
      /*  // This test case is deactivated, because internal transitions are not yet supported. It would replace the one above
      Arguments.of(
        List.of(msg("noop"), tk(), msg("internal"), tk(), tk()),
        List.of(msg("Enter A"),                              // <- Initial state entered
                msg("Do A"), tk(),                           // <- Action of first time frame
                msg("internal A"), msg("Do A"), tk(),  // <- Action of second time frame
                msg("Do A"), tk())                           // <- ...
      ),
      */
      Arguments.of(
        List.of(msg("loop"), msg("loop"), tk()),
        List.of(msg("Enter A"),
                msg("Exit A"), msg("A -> A"), msg("Enter A"),
                msg("Exit A"), msg("A -> A"), msg("Enter A"),
                msg("Do A"), tk())
      ),
      Arguments.of(
        List.of(msg("switch"), tk(), msg("switch"), tk(), tk()),
        List.of(msg("Enter A"),
                msg("Exit A"), msg("A -> B"), msg("Enter B"), msg("Do B"), tk(),
                msg("Exit B"), msg("B -> A"), msg("Enter A"), msg("Do A"), tk(),
                msg("Do A"), tk())
      ),
      Arguments.of(
        List.of(msg("switch"), tk(), msg("loop"), tk(), msg("loop"), tk()),
        List.of(msg("Enter A"),
                msg("Exit A"), msg("A -> B"), msg("Enter B"), msg("Do B"), tk(),
                msg("Exit B"), msg("B -> B"), msg("Enter B"), msg("Do B"), tk(),
                msg("Exit B"), msg("B -> B"), msg("Enter B"), msg("Do B"), tk())
      ),
      Arguments.of(
        List.of(msg("switch"), tk(), msg("noop"), tk(), msg("loop"), tk(), msg("switch"), tk(), tk()),
        List.of(msg("Enter A"),
                msg("Exit A"), msg("A -> B"), msg("Enter B"), msg("Do B"), tk(),
                msg("Do B"), tk(),
                msg("Exit B"), msg("B -> B"), msg("Enter B"), msg("Do B"), tk(),
                msg("Exit B"), msg("B -> A"), msg("Enter A"), msg("Do A"), tk(),
                msg("Do A"), tk())
      )
    );
  }
}
