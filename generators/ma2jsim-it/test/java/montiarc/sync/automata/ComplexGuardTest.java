/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.Person;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class ComplexGuardTest {

  /**
   * @param input_name the input stream on port nameToWhatever;
   * @param input_person the input stream on port person;
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<Map<String, String>>> input_name,
              @NotNull List<Message<Person>> input_person,
              @NotNull List<Message<String>> expected) {
    Preconditions.checkNotNull(input_name);
    Preconditions.checkNotNull(input_person);
    Preconditions.checkNotNull(expected);

    // Given
    ComplexGuardComp sut = new ComplexGuardCompBuilder().setName("sut").build();
    PortObserver<String> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<Map<String, String>> msg : input_name) {
      sut.port_nameToWhatever().receive(msg);
    }

    for (Message<Person> msg : input_person) {
      sut.port_person().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    Person alice = new Person();
    Person gilbert = new Person();

    alice.name = "Alice";
    gilbert.name = "Gilbert";

    return Stream.of(
      Arguments.of(
        List.of(msg(Map.of("Alice", "Barker")), tk()),
        List.of(msg(alice), tk()),
        List.of(tk())
      ),
      Arguments.of(
        List.of(msg(Map.of("Harley", "Barker")), tk()),
        List.of(msg(gilbert), tk()),
        List.of(tk())
      ),
      Arguments.of(
        List.of(msg(Map.of("Gilbert", "Bar")), tk()),
        List.of(msg(gilbert), tk()),
        List.of(tk())
      ),
      Arguments.of(
        List.of(msg(Map.of("Gilbert", "Foo")), tk()),
        List.of(msg(gilbert), tk()),
        List.of(msg("success"), tk())
      )
    );
  }
}
