/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.rte.port.PortObserver;
import montiarc.types.OnOff;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

class InverterTest {

  /**
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<OnOff>> input,
              @NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    InverterComp sut = new InverterCompBuilder().setName("sut").build();
    PortObserver<OnOff> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<OnOff> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(new Message<>(OnOff.ON), Tick.get()),
        List.of(new Message<>(OnOff.OFF), Tick.get())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), Tick.get()),
        List.of(new Message<>(OnOff.ON), Tick.get())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON), Tick.get()),
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.OFF), Tick.get()),
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.ON), Tick.get())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.ON), Tick.get()),
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.OFF), Tick.get())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get()),
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON), Tick.get())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON), Tick.get()),
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get())
      ));
  }
}
