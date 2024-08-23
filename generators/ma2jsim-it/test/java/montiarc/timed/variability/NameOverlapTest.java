/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.variability;

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

class NameOverlapTest {

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
    NameOverlapComp sut = new NameOverlapCompBuilder().setName("sut").set_feature_onOff(true).build();
    PortObserver<OnOff> port_o = new PortObserver<>();

    sut.port_o0().connect(port_o);

    // When
    sut.init();

    for (Message<OnOff> msg : input) {
      sut.port_i0().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON))
      ));
  }

  /**
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("ioInt")
  void testIOInt(@NotNull List<Message<Integer>> input,
              @NotNull List<Message<Integer>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    NameOverlapComp sut = new NameOverlapCompBuilder().setName("sut").set_feature_onOff(false).build();
    PortObserver<Number> port_o = new PortObserver<>();

    sut.port_o1().connect(port_o);

    // When
    sut.init();

    for (Message<Integer> msg : input) {
      sut.port_i1().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> ioInt() {
    return Stream.of(
      Arguments.of(
        List.of(new Message<>(0)),
        List.of(new Message<>(0))
      ),
      Arguments.of(
        List.of(new Message<>(0), Tick.get(), new Message<>(1)),
        List.of(new Message<>(0), Tick.get(), new Message<>(1))
      ),
      Arguments.of(
        List.of(new Message<>(-1), Tick.get(), new Message<>(5), Tick.get(), new Message<>(100)),
        List.of(new Message<>(-1), Tick.get(), new Message<>(5), Tick.get(), new Message<>(100))
      ));
  }
}
