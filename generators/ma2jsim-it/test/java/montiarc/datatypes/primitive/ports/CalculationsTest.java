/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.ports;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static org.assertj.core.api.Assertions.assertThat;

class CalculationsTest {

  @Test
  void testCorrectnessOfCalculations() {
    // Test data
    List<Message<Number>> byteInput =
      List.of(msg((byte) 1), tk(), msg((byte) -2), tk(), msg((byte) 0), tk());
    List<Message<Number>> expectedByteOut =
      List.of(msg((byte) 1), tk(), msg((byte) -2), tk(), msg((byte) 0), tk());

    List<Message<Number>> shortInput =
      List.of(msg((short) 1), tk(), msg((short) -2), tk(), msg((short) 0), tk());
    List<Message<Number>> expectedShortOut =
      List.of(msg((short) 1), tk(), msg((short) -2), tk(), msg((short) 0), tk());

    List<Message<Number>> intInput          = List.of(msg(1), tk(), msg(-2), tk(), msg(0), tk());
    List<Message<Number>> expectedIntOutput = List.of(msg(-1), tk(), msg(2), tk(), msg(0), tk());

    List<Message<Number>> longInput          = List.of(msg(1L), tk(), msg(-2L), tk(), msg(0L), tk());
    List<Message<Number>> expectedLongOutput = List.of(msg(-1L), tk(), msg(2L), tk(), msg(0L), tk());

    List<Message<Number>> floatInput          = List.of(msg(1.0f), tk(), msg(-2.0f), tk(), msg(0.0f), tk());
    List<Message<Number>> expectedFloatOutput = List.of(msg(-1.0f), tk(), msg(2.0f), tk(), msg(-0.0f), tk());

    List<Message<Number>> doubleInput          = List.of(msg(1.0), tk(), msg(-2.0), tk(), msg(0.0), tk());
    List<Message<Number>> expectedDoubleOutput = List.of(msg(-1.0), tk(), msg(2.0), tk(), msg(-0.0), tk());

    List<Message<Number>> charInput =
      List.of(msg((int) 'a'), tk(), msg((int) '-'), tk(), msg((int) '0'), tk());
    List<Message<Number>> expectedCharOut =
      List.of(msg((int) 'a'), tk(), msg((int) '-'), tk(), msg((int) '0'), tk());

    List<Message<Boolean>> boolInput       = List.of(msg(true), tk(), msg(false), tk(), msg(false), tk());
    List<Message<Boolean>> expectedBoolOut = List.of(msg(false), tk(), msg(true), tk(), msg(true), tk());

    PortObserver<Number> port_byte = new PortObserver<>();
    PortObserver<Number> port_short = new PortObserver<>();
    PortObserver<Number> port_int = new PortObserver<>();
    PortObserver<Number> port_long = new PortObserver<>();
    PortObserver<Number> port_float = new PortObserver<>();
    PortObserver<Number> port_double = new PortObserver<>();
    PortObserver<Number> port_char = new PortObserver<>();
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
