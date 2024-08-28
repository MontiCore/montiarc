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

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

class MediumTest {

  /**
   * @param input    the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull List<Message<OnOff>> input,
              @NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    MediumComp sut = new MediumCompBuilder().setName("sut").build();
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
      /*
      Arguments.of(
        List.of(msg(OnOff.ON), msg(OnOff.ON)),
        List.of(msg(OnOff.ON))
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), msg(OnOff.OFF)),
        List.of(msg(OnOff.ON))
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), msg(OnOff.ON)),
        List.of(msg(OnOff.OFF))
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), msg(OnOff.OFF)),
        List.of(msg(OnOff.OFF))
      ),
      */
      Arguments.of(
        List.of(tk()),
        List.of(tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk()),
        List.of(msg(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk()),
        List.of(msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(tk(), msg(OnOff.ON), tk()),
        List.of(tk(), msg(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(tk(), msg(OnOff.OFF), tk()),
        List.of(tk(), msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON), tk()),
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), tk(), msg(OnOff.OFF), tk()),
        List.of(msg(OnOff.ON), tk(), msg(OnOff.OFF), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF), tk())
      ),
      /*
      Arguments.of(
        List.of(msg(OnOff.ON), msg(OnOff.ON), tk(), msg(OnOff.ON)),
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON))
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), msg(OnOff.ON), tk(), msg(OnOff.OFF)),
        List.of(msg(OnOff.ON), tk(), msg(OnOff.OFF))
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), msg(OnOff.OFF), tk(), msg(OnOff.ON)),
        List.of(msg(OnOff.ON), tk(), msg(OnOff.ON))
      ),
      Arguments.of(
        List.of(msg(OnOff.ON), msg(OnOff.OFF), tk(), msg(OnOff.OFF)),
        List.of(msg(OnOff.ON), tk(), msg(OnOff.OFF))
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), msg(OnOff.ON), tk(), msg(OnOff.ON)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON))
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), msg(OnOff.ON), tk(), msg(OnOff.OFF)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF))
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), msg(OnOff.OFF), tk(), msg(OnOff.ON)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON))
      ),
      Arguments.of(
        List.of(msg(OnOff.OFF), msg(OnOff.OFF), tk(), msg(OnOff.OFF)),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.OFF))
      ),
      */
      Arguments.of(
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON), tk(), msg(OnOff.ON), tk()),
        List.of(msg(OnOff.OFF), tk(), msg(OnOff.ON), tk(), msg(OnOff.ON), tk())
      ),
      Arguments.of(
        List.of(tk(), tk()),
        List.of(tk(), tk())
      ));
  }
}
