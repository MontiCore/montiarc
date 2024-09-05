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
class TSDelayBooleanTest {

  @ParameterizedTest
  @MethodSource("io")
  void test(@NotNull Boolean iv,
            @NotNull List<Message<Boolean>> i,
            @NotNull List<Message<Boolean>> o) {
    // Given
    TSDelayBooleanComp sut = new TSDelayBooleanCompBuilder().setName("sut")
      .set_param_iv(iv).build();
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
      // true x <true> -> <true>
      Arguments.of(
        true,
        List.of(msg(true)),
        List.of(msg(true), tk())
      ),
      // true x <false> -> <true>
      Arguments.of(
        true,
        List.of(msg(false)),
        List.of(msg(true), tk())
      ),
      // false x <true> -> <false>
      Arguments.of(
        false,
        List.of(msg(true)),
        List.of(msg(false), tk())
      ),
      // false x <false> -> <false>
      Arguments.of(
        false,
        List.of(msg(false)),
        List.of(msg(false), tk())
      ),
      // true x <true, true> -> <true, true>
      Arguments.of(
        true,
        List.of(msg(true), tk(), msg(true)),
        List.of(msg(true), tk(), msg(true), tk())
      ),
      // true x <true, false> -> <true, true>
      Arguments.of(
        true,
        List.of(msg(true), tk(), msg(false)),
        List.of(msg(true), tk(), msg(true), tk())
      ),
      // true x <false, true> -> <true, false>
      Arguments.of(
        true,
        List.of(msg(false), tk(), msg(true)),
        List.of(msg(true), tk(), msg(false), tk())
      ),
      // true x <false, false> -> <true, false>
      Arguments.of(
        true,
        List.of(msg(false), tk(), msg(false)),
        List.of(msg(true), tk(), msg(false), tk())
      ),
      // false x <true, true> -> <false, true>
      Arguments.of(
        false,
        List.of(msg(true), tk(), msg(true)),
        List.of(msg(false), tk(), msg(true), tk())
      ),
      // false x <true, false> -> <false, true>
      Arguments.of(
        false,
        List.of(msg(true), tk(), msg(false)),
        List.of(msg(false), tk(), msg(true), tk())
      ),
      // false x <false, true> -> <false, false>
      Arguments.of(
        false,
        List.of(msg(false), tk(), msg(true)),
        List.of(msg(false), tk(), msg(false), tk())
      ),
      // false x <false, false> -> <false, false>
      Arguments.of(
        false,
        List.of(msg(false), tk(), msg(false)),
        List.of(msg(false), tk(), msg(false), tk())
      )
    );
  }
}
