/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;

@JSimTest
class ParamBool4Test {

  /**
   * @param p the argument for parameter p
   * @param input the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(boolean p, @NotNull List<Message<Boolean>> input,
              @NotNull List<Message<Boolean>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    ParamBool4Comp sut = new ParamBool4CompBuilder()
      .set_param_p(p)
      .setName("sut").build();
    PortObserver<Boolean> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<Boolean> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // 1
      Arguments.of(
        true,
        List.of(msg(true)),
        List.of(msg(true))
      ),
      // 2
      Arguments.of(
        true,
        List.of(msg(false)),
        List.of(msg(false))
      ),
      // 3
      Arguments.of(
        false,
        List.of(msg(true)),
        List.of()
      ),
      // 4
      Arguments.of(
        false,
        List.of(msg(false)),
        List.of()
      ),
      // 5
      Arguments.of(
        true,
        List.of(msg(true), msg(true)),
        List.of(msg(true), msg(true))
      ),
      // 6
      Arguments.of(
        true,
        List.of(msg(true), msg(false)),
        List.of(msg(true), msg(false))
      ),
      // 7
      Arguments.of(
        true,
        List.of(msg(false), msg(true)),
        List.of(msg(false), msg(true))
      ),
      // 8
      Arguments.of(
        true,
        List.of(msg(false), msg(false)),
        List.of(msg(false), msg(false))
      ),
      // 9
      Arguments.of(
        false,
        List.of(msg(true), msg(true)),
        List.of()
      ),
      // 10
      Arguments.of(
        false,
        List.of(msg(true), msg(false)),
        List.of()
      ),
      // 11
      Arguments.of(
        false,
        List.of(msg(false), msg(true)),
        List.of()
      ),
      // 12
      Arguments.of(
        false,
        List.of(msg(false), msg(false)),
        List.of()
      )
    );
  }
}
