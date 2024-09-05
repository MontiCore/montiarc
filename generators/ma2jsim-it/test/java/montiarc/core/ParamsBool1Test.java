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
class ParamsBool1Test {

  /**
   * @param p1 the argument for parameter p1
   * @param p2 the argument for parameter p2
   * @param input the input stream on port i
   * @param expected_o1 the expected output stream on port o1
   * @param expected_o2 the expected output stream on port o2
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(boolean p1, boolean p2,
              @NotNull List<Message<Boolean>> input,
              @NotNull List<Message<Boolean>> expected_o1,
              @NotNull List<Message<Boolean>> expected_o2) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected_o1);
    Preconditions.checkNotNull(expected_o2);

    // Given
    ParamsBool1Comp sut = new ParamsBool1CompBuilder()
      .set_param_p1(p1)
      .set_param_p2(p2)
      .setName("sut").build();
    PortObserver<Boolean> port_o1 = new PortObserver<>();
    PortObserver<Boolean> port_o2 = new PortObserver<>();

    sut.port_o1().connect(port_o1);
    sut.port_o2().connect(port_o2);

    // When
    sut.init();

    for (Message<Boolean> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o1.getObservedMessages()).containsExactlyElementsOf(expected_o1);
    Assertions.assertThat(port_o2.getObservedMessages()).containsExactlyElementsOf(expected_o2);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // 1
      Arguments.of(
        true, true,
        List.of(msg(true)),
        List.of(msg(true)),
        List.of(msg(true))
      ),
      // 2
      Arguments.of(
        true, true,
        List.of(msg(false)),
        List.of(msg(true)),
        List.of(msg(true))
      ),
      // 3
      Arguments.of(
        true, false,
        List.of(msg(true)),
        List.of(msg(true)),
        List.of(msg(false))
      ),
      // 4
      Arguments.of(
        true, false,
        List.of(msg(false)),
        List.of(msg(true)),
        List.of(msg(false))
      ),
      // 5
      Arguments.of(
        false, true,
        List.of(msg(true)),
        List.of(msg(false)),
        List.of(msg(true))
      ),
      // 6
      Arguments.of(
        false, true,
        List.of(msg(false)),
        List.of(msg(false)),
        List.of(msg(true))
      ),
      // 7
      Arguments.of(
        false, false,
        List.of(msg(true)),
        List.of(msg(false)),
        List.of(msg(false))
      ),
      // 8
      Arguments.of(
        false, false,
        List.of(msg(false)),
        List.of(msg(false)),
        List.of(msg(false))
      ),
      // 9
      Arguments.of(
        true, true,
        List.of(msg(true), msg(true)),
        List.of(msg(true), msg(true)),
        List.of(msg(true), msg(true))
      ),
      // 10
      Arguments.of(
        true, false,
        List.of(msg(true), msg(true)),
        List.of(msg(true), msg(true)),
        List.of(msg(false), msg(false))
      ),
      // 11
      Arguments.of(
        false, true,
        List.of(msg(true), msg(true)),
        List.of(msg(false), msg(false)),
        List.of(msg(true), msg(true))
      ),
      // 12
      Arguments.of(
        false, false,
        List.of(msg(true), msg(true)),
        List.of(msg(false), msg(false)),
        List.of(msg(false), msg(false))
      )
    );
  }
}
