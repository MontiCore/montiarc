/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.variability;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.OnOff;
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
        List.of(msg(OnOff.ON)),
        List.of(msg(OnOff.ON))
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF)),
        List.of(msg(OnOff.OFF))
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON)),
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON))
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk(), msg(OnOff.OFF)),
        List.of(msg(OnOff.ON), tk(), msg(OnOff.OFF))
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON))
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF))
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON), tk(), msg(OnOff.ON)),
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON), tk(), msg(OnOff.ON))
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
        List.of(msg(0)),
        List.of(msg(0))
      ),
      Arguments.of(
        List.of(msg(0), tk(), msg(1)),
        List.of(msg(0), tk(), msg(1))
      ),
      Arguments.of(
        List.of(msg(-1), tk(), msg(5), tk(), msg(100)),
        List.of(msg(-1), tk(), msg(5), tk(), msg(100))
      ));
  }
}
