/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.variability;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.ITimeAwareInPort;
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

import static montiarc.MsgFactory.msg;
import static montiarc.MsgFactory.tk;

@ExtendWith(MockitoExtension.class)
class FieldSourceTest {

  /**
   * capture of the actual output stream on port o
   */
  @Captor
  ArgumentCaptor<Message<Number>> actual;

  /**
   * the target port of output port o
   */
  @Mock
  ITimeAwareInPort<Number> port_o;

  /**
   * @param highPrecision the configuration of the sut
   * @param expected      the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("ioDelayed")
  void testIODelayed(@NotNull boolean highPrecision,
                     @NotNull List<Message<Number>> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    FieldSourceComp sut = new FieldSourceCompBuilder().setName("sut").set_feature_highPrecision(highPrecision).build();

    sut.port_o().connect(this.port_o);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_o).receive(this.actual.capture());

    // When
    sut.init();
    sut.getScheduler().run(3);

    // Then
    Assertions.assertThat(this.actual.getAllValues()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> ioDelayed() {
    return Stream.of(
      Arguments.of(
        false,
        List.of(msg(2.0), tk(), msg(3.0), tk(), msg(4.0), tk())
      ),
      Arguments.of(
        true,
        List.of(msg(2.5), tk(), msg(3.5), tk(), msg(4.5), tk())
      )
    );
  }
}
