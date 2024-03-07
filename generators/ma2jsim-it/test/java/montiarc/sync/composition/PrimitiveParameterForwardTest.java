/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.ITimeAwareInPort;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
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
class PrimitiveParameterForwardTest {

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
   * @param parameter the parameter p of the component
   * @param expected  the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@Nullable int parameter,
              @NotNull List<Message<Number>> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    PrimitiveParameterForwardComp sut = new PrimitiveParameterForwardCompBuilder().setName("sut").set_param_param(parameter).build();

    sut.port_o().connect(this.port_o);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_o).receive(this.actual.capture());

    // When
    sut.init();
    sut.handleTickEvent();

    // Then
    Assertions.assertThat(this.actual.getAllValues()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      Arguments.of(
        0,
        List.of(msg(0), tk())
      ),
      Arguments.of(
        2000,
        List.of(msg(2000), tk())
      ),
      Arguments.of(
        -1,
        List.of(msg(-1), tk())
      )
    );
  }
}
