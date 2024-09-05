/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class TSDelayShortTest {

  @ParameterizedTest
  @MethodSource("io")
  void test(@NotNull Short iv,
            @NotNull List<Message<Short>> i,
            @NotNull List<Message<Short>> o) {
    // Given
    TSDelayShortComp sut = new TSDelayShortCompBuilder().setName("sut")
      .set_param_iv(iv).build();
    PortObserver<Short> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<Short> msg : i) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(o);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // MIN x <MIN> -> <MIN>
      Arguments.of(
        Short.MIN_VALUE,
        List.of(msg(Short.MIN_VALUE)),
        List.of(msg(Short.MIN_VALUE), tk())
      ),
      // MIN x <MAX> -> <MIN>
      Arguments.of(
        Short.MIN_VALUE,
        List.of(msg(Short.MAX_VALUE)),
        List.of(msg(Short.MIN_VALUE), tk())
      ),
      // MAX x <MIN> -> <MAX>
      Arguments.of(
        Short.MAX_VALUE,
        List.of(msg(Short.MIN_VALUE)),
        List.of(msg(Short.MAX_VALUE), tk())
      ),
      // MAX x <MAX> -> <MAX>
      Arguments.of(
        Short.MAX_VALUE,
        List.of(msg(Short.MAX_VALUE)),
        List.of(msg(Short.MAX_VALUE), tk())
      ),
      // MIN x <MIN, MIN> -> <MIN, MIN>
      Arguments.of(
        Short.MIN_VALUE,
        List.of(msg(Short.MIN_VALUE), tk(), msg(Short.MIN_VALUE)),
        List.of(msg(Short.MIN_VALUE), tk(), msg(Short.MIN_VALUE), tk())
      ),
      // MIN x <MIN, MAX> -> <MIN, MIN>
      Arguments.of(
        Short.MIN_VALUE,
        List.of(msg(Short.MIN_VALUE), tk(), msg(Short.MAX_VALUE)),
        List.of(msg(Short.MIN_VALUE), tk(), msg(Short.MIN_VALUE), tk())
      ),
      // MIN x <MAX, MIN> -> <MIN, MAX>
      Arguments.of(
        Short.MIN_VALUE,
        List.of(msg(Short.MAX_VALUE), tk(), msg(Short.MIN_VALUE)),
        List.of(msg(Short.MIN_VALUE), tk(), msg(Short.MAX_VALUE), tk())
      ),
      // MIN x <MAX, MAX> -> <MIN, MAX>
      Arguments.of(
        Short.MIN_VALUE,
        List.of(msg(Short.MAX_VALUE), tk(), msg(Short.MAX_VALUE)),
        List.of(msg(Short.MIN_VALUE), tk(), msg(Short.MAX_VALUE), tk())
      ),
      // MAX x <MIN, MIN> -> <MAX, MIN>
      Arguments.of(
        Short.MAX_VALUE,
        List.of(msg(Short.MIN_VALUE), tk(), msg(Short.MIN_VALUE)),
        List.of(msg(Short.MAX_VALUE), tk(), msg(Short.MIN_VALUE), tk())
      ),
      // MAX x <MIN, MAX> -> <MAX, MIN>
      Arguments.of(
        Short.MAX_VALUE,
        List.of(msg(Short.MIN_VALUE), tk(), msg(Short.MAX_VALUE)),
        List.of(msg(Short.MAX_VALUE), tk(), msg(Short.MIN_VALUE), tk())
      ),
      // MAX x <MAX, MIN> -> <MAX, MAX>
      Arguments.of(
        Short.MAX_VALUE,
        List.of(msg(Short.MAX_VALUE), tk(), msg(Short.MIN_VALUE)),
        List.of(msg(Short.MAX_VALUE), tk(), msg(Short.MAX_VALUE), tk())
      ),
      // MAX x <MAX, MAX> -> <MAX, MAX>
      Arguments.of(
        Short.MAX_VALUE,
        List.of(msg(Short.MAX_VALUE), tk(), msg(Short.MAX_VALUE)),
        List.of(msg(Short.MAX_VALUE), tk(), msg(Short.MAX_VALUE), tk())
      )
    );
  }
}
