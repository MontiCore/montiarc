/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

import montiarc.example.computers.instruction.z1.Types.AluOp;
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
class ArithmeticUnitTest {

  static final int ZERO = 1048576;
  static final int ONE = 0;
  static final int ONE_AND_A_HALF = 8192;
  static final int THREE = 24576;
  static final int FOUR = 32768;
  static final int SIX = 40960;
  static final int THREE_QUARTERS = 2088960;
  static final int MINUS_THREE = 2121728;
  static final int INFINITY = 1032192;

  /**
   * @param op     the input stream on port op
   * @param a      the input stream on port a
   * @param b      the input stream on port b
   * @param result the expected output stream on port result
   */
  @ParameterizedTest
  @MethodSource("io")
  void test(@NotNull List<Message<AluOp>> op,
            @NotNull List<Message<Integer>> a,
            @NotNull List<Message<Integer>> b,
            @NotNull List<Message<Integer>> result) {
    // Given
    ArithmeticUnitComp sut = new ArithmeticUnitCompBuilder().setName("sut").build();
    PortObserver<Integer> port_result = new PortObserver<>();

    sut.port_result().connect(port_result);

    // When
    for (Message<AluOp> msg : op) {
      sut.port_op().receive(msg);
    }
    for (Message<Integer> msg : a) {
      sut.port_a().receive(msg);
    }
    for (Message<Integer> msg : b) {
      sut.port_b().receive(msg);
    }

    sut.runToCompletion();

    // Then
    assertThat(port_result.getObservedMessages()).containsExactlyElementsOf(result);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // none: 3 -> 3
      Arguments.of(
        List.of(msg(AluOp.NONE), tk()),
        List.of(msg(THREE), tk()),
        List.of(msg(FOUR), tk()),
        List.of(msg(THREE), tk())
      ),
      // 1.5 + 1.5 = 3
      Arguments.of(
        List.of(msg(AluOp.ADD), tk()),
        List.of(msg(ONE_AND_A_HALF), tk()),
        List.of(msg(ONE_AND_A_HALF), tk()),
        List.of(msg(THREE), tk())
      ),
      // 1 + 0 = 1
      Arguments.of(
        List.of(msg(AluOp.ADD), tk()),
        List.of(msg(ONE), tk()),
        List.of(msg(ZERO), tk()),
        List.of(msg(ONE), tk())
      ),
      // 1 - 4 = -3
      Arguments.of(
        List.of(msg(AluOp.SUB), tk()),
        List.of(msg(ONE), tk()),
        List.of(msg(FOUR), tk()),
        List.of(msg(MINUS_THREE), tk())
      ),
      // 3 - 3 = 0
      Arguments.of(
        List.of(msg(AluOp.SUB), tk()),
        List.of(msg(THREE), tk()),
        List.of(msg(THREE), tk()),
        List.of(msg(ZERO), tk())
      ),
      // 1.5 * 4 = 6
      Arguments.of(
        List.of(msg(AluOp.MUL), tk()),
        List.of(msg(ONE_AND_A_HALF), tk()),
        List.of(msg(FOUR), tk()),
        List.of(msg(SIX), tk())
      ),
      // 3 * 0 = 0
      Arguments.of(
        List.of(msg(AluOp.MUL), tk()),
        List.of(msg(THREE), tk()),
        List.of(msg(ZERO), tk()),
        List.of(msg(ZERO), tk())
      ),
      // 3 / 4 = 0.75
      Arguments.of(
        List.of(msg(AluOp.DIV), tk()),
        List.of(msg(THREE), tk()),
        List.of(msg(FOUR), tk()),
        List.of(msg(THREE_QUARTERS), tk())
      ),
      // 1 / 0 = infinity
      Arguments.of(
        List.of(msg(AluOp.DIV), tk()),
        List.of(msg(ONE), tk()),
        List.of(msg(ZERO), tk()),
        List.of(msg(INFINITY), tk())
      ),
      // 1.5 * 4 = 6, 6 / 4 = 1.5
      Arguments.of(
        List.of(msg(AluOp.MUL), tk(), msg(AluOp.DIV), tk()),
        List.of(msg(ONE_AND_A_HALF), tk(), msg(SIX), tk()),
        List.of(msg(FOUR), tk(), msg(FOUR), tk()),
        List.of(msg(SIX), tk(), msg(ONE_AND_A_HALF), tk())
      )
    );
  }
}
