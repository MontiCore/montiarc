/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class PrimitiveParameterForwardTest {

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
    PrimitiveParameterForwardComp sut = new PrimitiveParameterForwardCompBuilder()
      .setName("sut").set_param_param(parameter).build();
    PortObserver<Number> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();
    sut.run(1);

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
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
