/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static org.assertj.core.api.Assertions.assertThat;

class DelayBooleanTest {

  @ParameterizedTest
  @MethodSource("io")
  void test(@NotNull List<Message<Boolean>> i,
            @NotNull List<Message<Boolean>> o) {
    // Given
    DelayBooleanComp sut = new DelayBooleanCompBuilder().setName("sut").build();
    PortObserver<Boolean> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<Boolean> msg : i) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(o);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // <true> -> <tk, true>
      Arguments.of(
        List.of(msg(true)),
        List.of(tk(), msg(true))
      ),
      // <false> -> <tk, false>
      Arguments.of(
        List.of(msg(false)),
        List.of(tk(), msg(false))
      ),
      // <true, true> -> <tk, true, true>
      Arguments.of(
        List.of(msg(true), msg(true)),
        List.of(tk(), msg(true), msg(true))
      ),
      // <true, false> -> <tk, true, false>
      Arguments.of(
        List.of(msg(true), msg(false)),
        List.of(tk(), msg(true), msg(false))
      ),
      // <false, true> -> <tk, false, true>
      Arguments.of(
        List.of(msg(false), msg(true)),
        List.of(tk(), msg(false), msg(true))
      ),
      // <false, false> -> <tk, false, false>
      Arguments.of(
        List.of(msg(false), msg(false)),
        List.of(tk(), msg(false), msg(false))
      ),
      // <true, tk, true> -> <tk, true, tk, true>
      Arguments.of(
        List.of(msg(true), tk(), msg(true)),
        List.of(tk(), msg(true), tk(), msg(true))
      ),
      // <true, tk, false> -> <tk, true, tk, false>
      Arguments.of(
        List.of(msg(true), tk(), msg(false)),
        List.of(tk(), msg(true), tk(), msg(false))
      ),
      // <false, tk, true> -> <tk, false, tk, true>
      Arguments.of(
        List.of(msg(false), tk(), msg(true)),
        List.of(tk(), msg(false), tk(), msg(true))
      ),
      // <false, tk, false> -> <tk, false, tk, false>
      Arguments.of(
        List.of(msg(false), tk(), msg(false)),
        List.of(tk(), msg(false), tk(), msg(false))
      )
    );
  }
}
