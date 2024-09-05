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
class DelayCharacterTest {

  @ParameterizedTest
  @MethodSource("io")
  void test(@NotNull List<Message<Character>> i,
            @NotNull List<Message<Character>> o) {
    // Given
    DelayCharacterComp sut = new DelayCharacterCompBuilder().setName("sut").build();
    PortObserver<Character> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<Character> msg : i) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(o);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // <MIN> -> <tk, MIN>
      Arguments.of(
        List.of(msg(Character.MIN_VALUE)),
        List.of(tk(), msg(Character.MIN_VALUE))
      ),
      // <MAX> -> <tk, MAX>
      Arguments.of(
        List.of(msg(Character.MAX_VALUE)),
        List.of(tk(), msg(Character.MAX_VALUE))
      ),
      // <MIN, MIN> -> <tk, MIN, MIN>
      Arguments.of(
        List.of(msg(Character.MIN_VALUE), msg(Character.MIN_VALUE)),
        List.of(tk(), msg(Character.MIN_VALUE), msg(Character.MIN_VALUE))
      ),
      // <MIN, MAX> -> <tk, MIN, MAX>
      Arguments.of(
        List.of(msg(Character.MIN_VALUE), msg(Character.MAX_VALUE)),
        List.of(tk(), msg(Character.MIN_VALUE), msg(Character.MAX_VALUE))
      ),
      // <MAX_VALUE, MIN> -> <tk, MAX, MIN>
      Arguments.of(
        List.of(msg(Character.MAX_VALUE), msg(Character.MIN_VALUE)),
        List.of(tk(), msg(Character.MAX_VALUE), msg(Character.MIN_VALUE))
      ),
      // <MAX_VALUE, MAX> -> <tk, MAX, MAX>
      Arguments.of(
        List.of(msg(Character.MAX_VALUE), msg(Character.MAX_VALUE)),
        List.of(tk(), msg(Character.MAX_VALUE), msg(Character.MAX_VALUE))
      ),
      // <MIN, tk, MIN> -> <tk, MIN, tk, MIN>
      Arguments.of(
        List.of(msg(Character.MIN_VALUE), tk(), msg(Character.MIN_VALUE)),
        List.of(tk(), msg(Character.MIN_VALUE), tk(), msg(Character.MIN_VALUE))
      ),
      // <MIN, tk, MAX> -> <tk, MIN, tk, MAX>
      Arguments.of(
        List.of(msg(Character.MIN_VALUE), tk(), msg(Character.MAX_VALUE)),
        List.of(tk(), msg(Character.MIN_VALUE), tk(), msg(Character.MAX_VALUE))
      ),
      // <MAX_VALUE, tk, MIN> -> <tk, MAX, tk, MIN>
      Arguments.of(
        List.of(msg(Character.MAX_VALUE), tk(), msg(Character.MIN_VALUE)),
        List.of(tk(), msg(Character.MAX_VALUE), tk(), msg(Character.MIN_VALUE))
      ),
      // <MAX_VALUE, tk, MAX> -> <tk, MAX, tk, MAX>
      Arguments.of(
        List.of(msg(Character.MAX_VALUE), tk(), msg(Character.MAX_VALUE)),
        List.of(tk(), msg(Character.MAX_VALUE), tk(), msg(Character.MAX_VALUE))
      )
    );
  }
}
