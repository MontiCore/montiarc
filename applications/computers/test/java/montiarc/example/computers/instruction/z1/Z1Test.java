/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

import montiarc.example.computers.instruction.z1.Types.Instruction;
import montiarc.example.computers.instruction.z1.Types.Opcode;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class Z1Test {

  /**
   * @param tape     the input stream on port tape
   * @param keyboard the input stream on port keyboard
   * @param display  the expected output stream on port display
   */
  @ParameterizedTest
  @MethodSource("io")
  void test(@NotNull List<Message<Instruction>> tape,
            @NotNull List<Message<Double>> keyboard,
            @NotNull List<Message<Double>> display) {
    // Given
    Z1Comp sut = new Z1CompBuilder().setName("sut").build();
    PortObserver<Double> port_display = new PortObserver<>();

    sut.port_display().connect(port_display);

    // When
    for (Message<Instruction> msg : tape) {
      sut.port_tape().receive(msg);
    }
    for (Message<Double> msg : keyboard) {
      sut.port_keyboard().receive(msg);
    }

    sut.runToCompletion();

    // Then
    assertThat(port_display.getObservedMessages()).containsExactlyElementsOf(display);
  }

  static Stream<Arguments> io() {
    return Stream.of(
      // ((1.5 + 2.5) * 3) / 4 = 3
      Arguments.of(
        stream(instr(Opcode.LU), instr(Opcode.LU), instr(Opcode.LS1), instr(Opcode.PS, 0),
          instr(Opcode.PR, 0), instr(Opcode.LU), instr(Opcode.LM), instr(Opcode.LU),
          instr(Opcode.LI), instr(Opcode.LD)),
        stream(1.5, 2.5, 0.0, 0.0, 0.0, 3.0, 0.0, 4.0, 0.0, 0.0),
        stream(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 3.0)
      ),
      // 2 - 5 = -3
      Arguments.of(
        stream(instr(Opcode.LU), instr(Opcode.LU), instr(Opcode.LS2), instr(Opcode.LD)),
        stream(2.0, 5.0, 0.0, 0.0),
        stream(0.0, 0.0, 0.0, -3.0)
      ),
      // the display keeps showing the last number
      Arguments.of(
        stream(instr(Opcode.LU), instr(Opcode.LD), instr(Opcode.LU), instr(Opcode.PS, 0)),
        stream(2.0, 0.0, 5.0, 0.0),
        stream(0.0, 2.0, 2.0, 2.0)
      ),
      // memory is initially zero
      Arguments.of(
        stream(instr(Opcode.LU), instr(Opcode.LD), instr(Opcode.PR, 63), instr(Opcode.LD)),
        stream(7.0, 0.0, 0.0, 0.0),
        stream(0.0, 7.0, 7.0, 0.0)
      ),
      // the first load goes to R1, the second to R2: 6 / 2 = 3
      Arguments.of(
        stream(instr(Opcode.LU), instr(Opcode.PS, 1), instr(Opcode.LU), instr(Opcode.PS, 2),
          instr(Opcode.PR, 1), instr(Opcode.PR, 2), instr(Opcode.LI), instr(Opcode.LD)),
        stream(6.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0),
        stream(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 3.0)
      ),
      // 1 / 0 = infinity
      Arguments.of(
        stream(instr(Opcode.LU), instr(Opcode.LU), instr(Opcode.LI), instr(Opcode.LD)),
        stream(1.0, 0.0, 0.0, 0.0),
        stream(0.0, 0.0, 0.0, Double.POSITIVE_INFINITY)
      ),
      // 1 / 3 is truncated to 14 mantissa bits
      Arguments.of(
        stream(instr(Opcode.LU), instr(Opcode.LU), instr(Opcode.LI), instr(Opcode.LD)),
        stream(1.0, 3.0, 0.0, 0.0),
        stream(0.0, 0.0, 0.0, 0.33331298828125)
      )
    );
  }

  private static Instruction instr(Opcode op) {
    return instr(op, 0);
  }

  private static Instruction instr(Opcode op, int address) {
    Instruction instruction = new Instruction();
    instruction.op = op;
    instruction.address = address;
    return instruction;
  }

  /**
   * @return a stream with one message per time slice
   */
  @SafeVarargs
  private static <T> List<Message<T>> stream(T... values) {
    List<Message<T>> messages = new ArrayList<>();
    for (T value : values) {
      messages.add(msg(value));
      messages.add(tk());
    }
    return messages;
  }
}
