/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import com.google.common.base.Preconditions;
import montiarc.rte.automaton.State;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.types.OnOff;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class SinkTest {

  @ParameterizedTest
  @MethodSource("io")
  void testST(@NotNull List<Message<OnOff>> inputs,
              @NotNull List<State> states) {
    Preconditions.checkNotNull(inputs);
    Preconditions.checkNotNull(states);

    // Given
    SinkComp sut = new SinkCompBuilder().setName("sut").build();

    List<State> actual = new ArrayList<>(states.size());

    // When
    sut.init();

    for (Message<OnOff> input : inputs) {
      sut.port_i().receive(input);
      sut.port_i().receive(Tick.get());

      actual.add(sut.getBehavior().getState());
    }

    // Then
    Assertions.assertThat(actual).containsExactlyElementsOf(states);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // 1
      Arguments.of(
        List.of(new Message<>(OnOff.ON)),
        List.of(SinkStates.state_On)
      ),
      // 2
      Arguments.of(
        List.of(new Message<>(OnOff.OFF)),
        List.of(SinkStates.state_Off)
      ),
      // 3
      Arguments.of(
        List.of(new Message<>(OnOff.ON), new Message<>(OnOff.ON)),
        List.of(SinkStates.state_On, SinkStates.state_On)
      ),
      // 4
      Arguments.of(
        List.of(new Message<>(OnOff.ON), new Message<>(OnOff.OFF)),
        List.of(SinkStates.state_On, SinkStates.state_Off)
      ),
      // 5
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), new Message<>(OnOff.ON)),
        List.of(SinkStates.state_Off, SinkStates.state_On)
      ),
      // 6
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), new Message<>(OnOff.OFF)),
        List.of(SinkStates.state_Off, SinkStates.state_Off)
      ),
      // 7
      Arguments.of(
        List.of(new Message<>(OnOff.ON), new Message<>(OnOff.ON), new Message<>(OnOff.ON)),
        List.of(SinkStates.state_On, SinkStates.state_On, SinkStates.state_On)
      ));
  }
}
