/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.variability;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

class FieldSourceTest {

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
    PortObserver<Number> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();
    sut.run(3);

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
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
