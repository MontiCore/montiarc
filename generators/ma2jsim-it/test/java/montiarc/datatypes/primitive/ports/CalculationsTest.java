/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.ports;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class CalculationsTest {

  @Test
  void testCorrectnessOfCalculations() {
    // Test data
    List<Message<Byte>> byteInput =
      List.of(msg((byte) 1), tk(), msg((byte) -2), tk(), msg((byte) 0), tk());
    List<Message<Byte>> expectedByteOut =
      List.of(msg((byte) 1), tk(), msg((byte) -2), tk(), msg((byte) 0), tk());

    List<Message<Short>> shortInput =
      List.of(msg((short) 1), tk(), msg((short) -2), tk(), msg((short) 0), tk());
    List<Message<Short>> expectedShortOut =
      List.of(msg((short) 1), tk(), msg((short) -2), tk(), msg((short) 0), tk());

    List<Message<Integer>> intInput          = List.of(msg(1), tk(), msg(-2), tk(), msg(0), tk());
    List<Message<Integer>> expectedIntOutput = List.of(msg(-1), tk(), msg(2), tk(), msg(0), tk());

    List<Message<Long>> longInput          = List.of(msg(1L), tk(), msg(-2L), tk(), msg(0L), tk());
    List<Message<Long>> expectedLongOutput = List.of(msg(-1L), tk(), msg(2L), tk(), msg(0L), tk());

    List<Message<Float>> floatInput          = List.of(msg(1.0f), tk(), msg(-2.0f), tk(), msg(0.0f), tk());
    List<Message<Float>> expectedFloatOutput = List.of(msg(-1.0f), tk(), msg(2.0f), tk(), msg(-0.0f), tk());

    List<Message<Double>> doubleInput          = List.of(msg(1.0), tk(), msg(-2.0), tk(), msg(0.0), tk());
    List<Message<Double>> expectedDoubleOutput = List.of(msg(-1.0), tk(), msg(2.0), tk(), msg(-0.0), tk());

    List<Message<Character>> charInput =
      List.of(msg('a'), tk(), msg('-'), tk(), msg('0'), tk());
    List<Message<Character>> expectedCharOut =
      List.of(msg('a'), tk(), msg('-'), tk(), msg('0'), tk());

    List<Message<Boolean>> boolInput       = List.of(msg(true), tk(), msg(false), tk(), msg(false), tk());
    List<Message<Boolean>> expectedBoolOut = List.of(msg(false), tk(), msg(true), tk(), msg(true), tk());

    PortObserver<Byte> port_byte = new PortObserver<>();
    PortObserver<Short> port_short = new PortObserver<>();
    PortObserver<Integer> port_int = new PortObserver<>();
    PortObserver<Long> port_long = new PortObserver<>();
    PortObserver<Float> port_float = new PortObserver<>();
    PortObserver<Double> port_double = new PortObserver<>();
    PortObserver<Character> port_char = new PortObserver<>();
    PortObserver<Boolean> port_boolean = new PortObserver<>();

    // Given
    CalculationsComp sut = new CalculationsCompBuilder().setName("sut").build();
    sut.port_outByte().connect(port_byte);
    sut.port_outShort().connect(port_short);
    sut.port_outInt().connect(port_int);
    sut.port_outLong().connect(port_long);
    sut.port_outFloat().connect(port_float);
    sut.port_outDouble().connect(port_double);
    sut.port_outChar().connect(port_char);
    sut.port_outBoolean().connect(port_boolean);

    // When
    sut.init();
    for (int i = 0; i < byteInput.size(); i++) {
      sut.port_inByte.receive(byteInput.get(i));
      sut.port_inShort.receive(shortInput.get(i));
      sut.port_inInt.receive(intInput.get(i));
      sut.port_inLong.receive(longInput.get(i));
      sut.port_inFloat.receive(floatInput.get(i));
      sut.port_inDouble.receive(doubleInput.get(i));
      sut.port_inChar.receive(charInput.get(i));
      sut.port_inBoolean.receive(boolInput.get(i));
    }

    sut.run();

    // Then
    Assertions.assertAll(
      () -> assertThat(port_byte.getObservedMessages()).as("bytes").containsExactlyElementsOf(expectedByteOut),
      () -> assertThat(port_short.getObservedMessages()).as("shorts").containsExactlyElementsOf(expectedShortOut),
      () -> assertThat(port_int.getObservedMessages()).as("ints").containsExactlyElementsOf(expectedIntOutput),
      () -> assertThat(port_long.getObservedMessages()).as("longs").containsExactlyElementsOf(expectedLongOutput),
      () -> assertThat(port_float.getObservedMessages()).as("floats").containsExactlyElementsOf(expectedFloatOutput),
      () -> assertThat(port_double.getObservedMessages()).as("doubles").containsExactlyElementsOf(expectedDoubleOutput),
      () -> assertThat(port_char.getObservedMessages()).as("chars").containsExactlyElementsOf(expectedCharOut),
      () -> assertThat(port_boolean.getObservedMessages()).as("booleans").containsExactlyElementsOf(expectedBoolOut)
    );
  }
}
