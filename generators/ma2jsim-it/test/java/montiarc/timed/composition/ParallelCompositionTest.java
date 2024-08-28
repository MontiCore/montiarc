/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

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

public class ParallelCompositionTest {

  /**
   * @param input_i1    the input stream on port i1
   * @param input_i2    the input stream on port i2
   * @param expected_o1 the expected output stream on port o1
   * @param expected_o2 the expected output stream on port o2
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<OnOff>> input_i1,
              @NotNull List<Message<OnOff>> input_i2,
              @NotNull List<Message<OnOff>> expected_o1,
              @NotNull List<Message<OnOff>> expected_o2) {
    Preconditions.checkNotNull(input_i1);
    Preconditions.checkNotNull(input_i2);
    Preconditions.checkNotNull(expected_o1);
    Preconditions.checkNotNull(expected_o2);

    // Given
    ParallelCompositionComp sut = new ParallelCompositionCompBuilder().setName("sut").build();
    PortObserver<OnOff> port_o1 = new PortObserver<>();
    PortObserver<OnOff> port_o2 = new PortObserver<>();

    sut.port_o1().connect(port_o1);
    sut.port_o2().connect(port_o2);
    // When
    sut.init();

    for (Message<OnOff> msg : input_i1) {
      sut.port_i1().receive(msg);
    }

    for (Message<OnOff> msg : input_i2) {
      sut.port_i2().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o1.getObservedMessages()).containsExactlyElementsOf(expected_o1);
    Assertions.assertThat(port_o2.getObservedMessages()).containsExactlyElementsOf(expected_o2);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(new Message<>(OnOff.ON)),
        List.of(),
        List.of(new Message<>(OnOff.ON)),
        List.of()
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF)),
        List.of(),
        List.of(new Message<>(OnOff.OFF)),
        List.of()
      ),
      Arguments.of(
        List.of(),
        List.of(new Message<>(OnOff.ON)),
        List.of(),
        List.of(new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(),
        List.of(new Message<>(OnOff.OFF)),
        List.of(),
        List.of(new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(tk()),
        List.of(tk()),
        List.of(tk()),
        List.of(tk())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk()),
        List.of(tk()),
        List.of(new Message<>(OnOff.ON), tk()),
        List.of(tk())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk()),
        List.of(tk()),
        List.of(new Message<>(OnOff.OFF), tk()),
        List.of(tk())
      ),
      Arguments.of(
        List.of(tk()),
        List.of(new Message<>(OnOff.ON), tk()),
        List.of(tk()),
        List.of(new Message<>(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(tk()),
        List.of(new Message<>(OnOff.OFF), tk()),
        List.of(tk()),
        List.of(new Message<>(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk()),
        List.of(new Message<>(OnOff.ON), tk()),
        List.of(new Message<>(OnOff.ON), tk()),
        List.of(new Message<>(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk()),
        List.of(new Message<>(OnOff.OFF), tk()),
        List.of(new Message<>(OnOff.OFF), tk()),
        List.of(new Message<>(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk()),
        List.of(new Message<>(OnOff.ON), tk()),
        List.of(new Message<>(OnOff.OFF), tk()),
        List.of(new Message<>(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk()),
        List.of(new Message<>(OnOff.OFF), tk()),
        List.of(new Message<>(OnOff.OFF), tk()),
        List.of(new Message<>(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(tk()),
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(tk())
      ),
      Arguments.of(
        List.of(tk(), new Message<>(OnOff.OFF)),
        List.of(tk()),
        List.of(tk(), new Message<>(OnOff.OFF)),
        List.of(tk())
      ),
      Arguments.of(
        List.of(tk()),
        List.of(tk(), new Message<>(OnOff.OFF)),
        List.of(tk()),
        List.of(tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(tk()),
        List.of(tk(), new Message<>(OnOff.OFF)),
        List.of(tk()),
        List.of(tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(tk(), tk()),
        List.of(tk(), tk()),
        List.of(tk(), tk()),
        List.of(tk(), tk())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), tk()),
        List.of(new Message<>(OnOff.ON), tk(), tk()),
        List.of(new Message<>(OnOff.ON), tk(), tk()),
        List.of(new Message<>(OnOff.OFF), tk(), tk())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), tk()),
        List.of(new Message<>(OnOff.OFF), tk(), tk()),
        List.of(new Message<>(OnOff.ON), tk(), tk()),
        List.of(new Message<>(OnOff.ON), tk(), tk())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), tk()),
        List.of(new Message<>(OnOff.ON), tk(), tk()),
        List.of(new Message<>(OnOff.OFF), tk(), tk()),
        List.of(new Message<>(OnOff.OFF), tk(), tk())
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), tk()),
        List.of(new Message<>(OnOff.OFF), tk(), tk()),
        List.of(new Message<>(OnOff.OFF), tk(), tk()),
        List.of(new Message<>(OnOff.ON), tk(), tk())
      ),
      Arguments.of(
        List.of(tk(), new Message<>(OnOff.ON), tk()),
        List.of(tk(), new Message<>(OnOff.ON), tk()),
        List.of(tk(), new Message<>(OnOff.ON), tk()),
        List.of(tk(), new Message<>(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(tk(), new Message<>(OnOff.ON), tk()),
        List.of(tk(), new Message<>(OnOff.OFF), tk()),
        List.of(tk(), new Message<>(OnOff.ON), tk()),
        List.of(tk(), new Message<>(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(tk(), new Message<>(OnOff.OFF), tk()),
        List.of(tk(), new Message<>(OnOff.ON), tk()),
        List.of(tk(), new Message<>(OnOff.OFF), tk()),
        List.of(tk(), new Message<>(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(tk(), new Message<>(OnOff.OFF), tk()),
        List.of(tk(), new Message<>(OnOff.OFF), tk()),
        List.of(tk(), new Message<>(OnOff.OFF), tk()),
        List.of(tk(), new Message<>(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(tk(), tk(), new Message<>(OnOff.ON)),
        List.of(tk(), tk(), new Message<>(OnOff.ON)),
        List.of(tk(), tk(), new Message<>(OnOff.ON)),
        List.of(tk(), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(tk(), tk(), new Message<>(OnOff.ON)),
        List.of(tk(), tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), tk(), new Message<>(OnOff.ON)),
        List.of(tk(), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(tk(), tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), tk(), new Message<>(OnOff.ON)),
        List.of(tk(), tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(tk(), tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), tk(), new Message<>(OnOff.OFF)),
        List.of(tk(), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON))
      ));
  }
}
