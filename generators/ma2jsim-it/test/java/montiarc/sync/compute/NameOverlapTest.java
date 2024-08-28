/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.types.OnOff;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.tk;

class NameOverlapTest {

  /**
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    NameOverlapComp sut = new NameOverlapCompBuilder().setName("sut").set_feature_onOff(true).build();
    PortObserver<OnOff> port_o = new PortObserver<>();

    sut.port_o0().connect(port_o);

    // When
    sut.init();
    sut.run((int) expected.stream().filter(msg -> !msg.equals(tk())).count());

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF), tk())
      ));
  }

  /**
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("ioInt")
  void testIOInt(@NotNull List<Message<Integer>> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    NameOverlapComp sut = new NameOverlapCompBuilder().setName("sut").set_feature_onOff(false).build();
    PortObserver<Integer> port_o = new PortObserver<>();

    sut.port_o1().connect(port_o);

    // When
    sut.init();

    sut.run((int) expected.stream().filter(msg -> !msg.equals(tk())).count());

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> ioInt() {
    return Stream.of(
      Arguments.of(
        List.of(new Message<>(0), tk())
      ),
      Arguments.of(
        List.of(new Message<>(0), tk(), new Message<>(0), tk())
      ),
      Arguments.of(
        List.of(new Message<>(0), tk(), new Message<>(0), tk(), new Message<>(0), tk())
      ));
  }
}
