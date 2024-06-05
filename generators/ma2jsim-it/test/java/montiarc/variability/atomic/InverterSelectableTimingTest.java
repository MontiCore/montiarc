/* (c) https://github.com/MontiCore/monticore */
package montiarc.variability.atomic;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.rte.port.ITimeAwareInPort;
import montiarc.types.OnOff;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class InverterSelectableTimingTest {

  /**
   * capture of the actual output stream on port o
   */
  @Captor
  ArgumentCaptor<Message<OnOff>> actual;

  /**
   * the target port of output port o
   */
  @Mock
  ITimeAwareInPort<OnOff> port_o;

  /**
   * @param input    the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull boolean sync,
              @NotNull List<Message<OnOff>> input,
              @NotNull List<Message<OnOff>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    InverterSelectableTimingComp sut = new InverterSelectableTimingCompBuilder().set_feature_sync(sync).setName("sut").build();

    sut.port_o().connect(this.port_o);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_o).receive(this.actual.capture());

    // When
    sut.init();

    for (Message<OnOff> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(this.actual.getAllValues()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        true,
        List.of(new Message<>(OnOff.ON), Tick.get()),
        List.of(new Message<>(OnOff.OFF), Tick.get())
      ),
      Arguments.of(
        true,
        List.of(new Message<>(OnOff.OFF), Tick.get()),
        List.of(new Message<>(OnOff.ON), Tick.get())
      ),
      Arguments.of(
        true,
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON), Tick.get()),
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get())
      ),
      Arguments.of(
        true,
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.OFF), Tick.get()),
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.ON), Tick.get())
      ),
      Arguments.of(
        true,
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.ON), Tick.get()),
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.OFF), Tick.get())
      ),
      Arguments.of(
        true,
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get()),
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON), Tick.get())
      ),
      Arguments.of(
        true,
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON), Tick.get()),
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get())
      ),
      Arguments.of(
        false,
        List.of(new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        false,
        List.of(new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON))
      ),
      Arguments.of(
        false,
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        false,
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        false,
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.OFF))
      ),
      Arguments.of(
        false,
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF)),
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON))
      ),
      Arguments.of(
        false,
        List.of(new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON), Tick.get(), new Message<>(OnOff.ON)),
        List.of(new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF), Tick.get(), new Message<>(OnOff.OFF))
      ));
  }
}
