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
class TSDelayByteTest {

  @ParameterizedTest
  @MethodSource("io")
  void test(@NotNull Byte iv,
            @NotNull List<Message<Byte>> i,
            @NotNull List<Message<Byte>> o) {
    // Given
    TSDelayByteComp sut = new TSDelayByteCompBuilder().setName("sut")
      .set_param_iv(iv).build();
    PortObserver<Byte> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<Byte> msg : i) {
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
        Byte.MIN_VALUE,
        List.of(msg(Byte.MIN_VALUE)),
        List.of(msg(Byte.MIN_VALUE), tk())
      ),
      // MIN x <MAX> -> <MIN>
      Arguments.of(
        Byte.MIN_VALUE,
        List.of(msg(Byte.MAX_VALUE)),
        List.of(msg(Byte.MIN_VALUE), tk())
      ),
      // MAX x <MIN> -> <MAX>
      Arguments.of(
        Byte.MAX_VALUE,
        List.of(msg(Byte.MIN_VALUE)),
        List.of(msg(Byte.MAX_VALUE), tk())
      ),
      // MAX x <MAX> -> <MAX>
      Arguments.of(
        Byte.MAX_VALUE,
        List.of(msg(Byte.MAX_VALUE)),
        List.of(msg(Byte.MAX_VALUE), tk())
      ),
      // MIN x <MIN, MIN> -> <MIN, MIN>
      Arguments.of(
        Byte.MIN_VALUE,
        List.of(msg(Byte.MIN_VALUE), tk(), msg(Byte.MIN_VALUE)),
        List.of(msg(Byte.MIN_VALUE), tk(), msg(Byte.MIN_VALUE), tk())
      ),
      // MIN x <MIN, MAX> -> <MIN, MIN>
      Arguments.of(
        Byte.MIN_VALUE,
        List.of(msg(Byte.MIN_VALUE), tk(), msg(Byte.MAX_VALUE)),
        List.of(msg(Byte.MIN_VALUE), tk(), msg(Byte.MIN_VALUE), tk())
      ),
      // MIN x <MAX, MIN> -> <MIN, MAX>
      Arguments.of(
        Byte.MIN_VALUE,
        List.of(msg(Byte.MAX_VALUE), tk(), msg(Byte.MIN_VALUE)),
        List.of(msg(Byte.MIN_VALUE), tk(), msg(Byte.MAX_VALUE), tk())
      ),
      // MIN x <MAX, MAX> -> <MIN, MAX>
      Arguments.of(
        Byte.MIN_VALUE,
        List.of(msg(Byte.MAX_VALUE), tk(), msg(Byte.MAX_VALUE)),
        List.of(msg(Byte.MIN_VALUE), tk(), msg(Byte.MAX_VALUE), tk())
      ),
      // MAX x <MIN, MIN> -> <MAX, MIN>
      Arguments.of(
        Byte.MAX_VALUE,
        List.of(msg(Byte.MIN_VALUE), tk(), msg(Byte.MIN_VALUE)),
        List.of(msg(Byte.MAX_VALUE), tk(), msg(Byte.MIN_VALUE), tk())
      ),
      // MAX x <MIN, MAX> -> <MAX, MIN>
      Arguments.of(
        Byte.MAX_VALUE,
        List.of(msg(Byte.MIN_VALUE), tk(), msg(Byte.MAX_VALUE)),
        List.of(msg(Byte.MAX_VALUE), tk(), msg(Byte.MIN_VALUE), tk())
      ),
      // MAX x <MAX, MIN> -> <MAX, MAX>
      Arguments.of(
        Byte.MAX_VALUE,
        List.of(msg(Byte.MAX_VALUE), tk(), msg(Byte.MIN_VALUE)),
        List.of(msg(Byte.MAX_VALUE), tk(), msg(Byte.MAX_VALUE), tk())
      ),
      // MAX x <MAX, MAX> -> <MAX, MAX>
      Arguments.of(
        Byte.MAX_VALUE,
        List.of(msg(Byte.MAX_VALUE), tk(), msg(Byte.MAX_VALUE)),
        List.of(msg(Byte.MAX_VALUE), tk(), msg(Byte.MAX_VALUE), tk())
      )
    );
  }
}
