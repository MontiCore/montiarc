/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.types.OnOff;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.tk;

class SwitchTest {

  /**
   * @param input_i1 the input stream on port i1
   * @param input_i2 the input stream on port i2
   * @param allowed_expected different allowed output streams on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  @Disabled("Because it is indeterministic")
  void testIO(@NotNull List<Message<OnOff>> input_i1,
              @NotNull List<Message<OnOff>> input_i2,
              @NotNull List<List<Message<OnOff>>> allowed_expected) {
    Preconditions.checkNotNull(input_i1);
    Preconditions.checkNotNull(input_i2);
    Preconditions.checkNotNull(allowed_expected);

    // Given
    SwitchComp sut = new SwitchCompBuilder().setName("sut").build();
    PortObserver<OnOff> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (int i = 0; i < input_i1.size(); i++) {
      sut.port_i1().receive(input_i1.get(i));
      sut.port_i2().receive(input_i2.get(i));
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).isIn(allowed_expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        List.of(new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON)),
        List.of(
          List.of(new Message<>(OnOff.ON)),
          List.of(new Message<>(OnOff.OFF))
        )
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF)),
        List.of(List.of(new Message<>(OnOff.OFF)))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON)),
        List.of(List.of(new Message<>(OnOff.OFF)))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF)),
        List.of(List.of(new Message<>(OnOff.OFF)))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(
          List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
          List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON))
        )
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(
          List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
          List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
          List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
          List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF))
        )
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)))
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(
          List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
          List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF))
        )
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.ON), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(
          List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
          List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON))
        )
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(
          List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
          List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF))
        )
      ),
      Arguments.of(
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.ON)),
        List.of(List.of(new Message<>(OnOff.OFF), tk(), new Message<>(OnOff.OFF)))
      )
    );
  }
}
