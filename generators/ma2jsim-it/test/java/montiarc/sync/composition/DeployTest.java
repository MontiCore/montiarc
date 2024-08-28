/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

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

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

class DeployTest {

  /**
   * @param expected1 the input stream on port i
   * @param expected2 the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("expected")
  void testIO(@NotNull List<Message<OnOff>> expected1,
              @NotNull List<Message<OnOff>> expected2) {
    Preconditions.checkNotNull(expected1);
    Preconditions.checkNotNull(expected2);

    // Given
    DeployComp sut = new DeployCompBuilder("sut").build();
    PortObserver<OnOff> port_o1 = new PortObserver<>();
    PortObserver<OnOff> port_o2 = new PortObserver<>();

    sut.subcomp_parallel().port_o1().connect(port_o1);
    sut.subcomp_parallel().port_o2().connect(port_o2);

    // When
    sut.init();
    sut.run(3);

    // Then
    Assertions.assertThat(port_o1.getObservedMessages()).as("parallel.o1").containsExactlyElementsOf(expected1);
    Assertions.assertThat(port_o2.getObservedMessages()).as("parallel.o2").containsExactlyElementsOf(expected2);
  }

  static Stream<Arguments> expected() {
    return Stream.of(
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk())
      ));
  }
}