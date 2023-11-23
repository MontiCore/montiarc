/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.ports;

import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.rte.port.ITimeAwareInPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
class CalculationsTest {

  /* Captures of the actual output streams on the different ports */
  @Captor ArgumentCaptor<Message<Number>> actualByte;
  @Captor ArgumentCaptor<Message<Number>> actualShort;
  @Captor ArgumentCaptor<Message<Number>> actualInt;
  @Captor ArgumentCaptor<Message<Number>> actualLong;
  @Captor ArgumentCaptor<Message<Number>> actualFloat;
  @Captor ArgumentCaptor<Message<Number>> actualDouble;
  @Captor ArgumentCaptor<Message<Number>> actualChar;
  @Captor ArgumentCaptor<Message<Boolean>> actualBoolean;

  /* Mocks for the target ports */
  @Mock ITimeAwareInPort<Number> port_byte;
  @Mock ITimeAwareInPort<Number> port_short;
  @Mock ITimeAwareInPort<Number> port_int;
  @Mock ITimeAwareInPort<Number> port_long;
  @Mock ITimeAwareInPort<Number> port_float;
  @Mock ITimeAwareInPort<Number> port_double;
  @Mock ITimeAwareInPort<Number> port_char;
  @Mock ITimeAwareInPort<Boolean> port_boolean;

  private static <T> Message<T> msg(T data) {
    return Message.of(data);
  }

  @Test
  void testCorrectnessOfCalculations() {
    // Test data
    List<Message<Number>> byteInput =
      List.of(msg((byte) 1), Tick.get(), msg((byte) -2), Tick.get(), msg((byte) 0));
    List<Message<Number>> expectedByteOut =
      List.of(msg((byte) 1), Tick.get(), msg((byte) -2), Tick.get(), msg((byte) 0));

    List<Message<Number>> shortInput =
      List.of(msg((short) 1), Tick.get(), msg((short) -2), Tick.get(), msg((short) 0));
    List<Message<Number>> expectedShortOut =
      List.of(msg((short) 1), Tick.get(), msg((short) -2), Tick.get(), msg((short) 0));

    List<Message<Number>> intInput          = List.of(msg(1), Tick.get(), msg(-2), Tick.get(), msg(0));
    List<Message<Number>> expectedIntOutput = List.of(msg(-1), Tick.get(), msg(2), Tick.get(), msg(0));

    List<Message<Number>> longInput          = List.of(msg(1L), Tick.get(), msg(-2L), Tick.get(), msg(0L));
    List<Message<Number>> expectedLongOutput = List.of(msg(-1L), Tick.get(), msg(2L), Tick.get(), msg(0L));

    List<Message<Number>> floatInput          = List.of(msg(1.0f), Tick.get(), msg(-2.0f), Tick.get(), msg(0.0f));
    List<Message<Number>> expectedFloatOutput = List.of(msg(-1.0f), Tick.get(), msg(2.0f), Tick.get(), msg(-0.0f));

    List<Message<Number>> doubleInput          = List.of(msg(1.0), Tick.get(), msg(-2.0), Tick.get(), msg(0.0));
    List<Message<Number>> expectedDoubleOutput = List.of(msg(-1.0), Tick.get(), msg(2.0), Tick.get(), msg(-0.0));

    List<Message<Number>> charInput =
      List.of(msg((int) 'a'), Tick.get(), msg((int) '-'), Tick.get(), msg((int) '0'));
    List<Message<Number>> expectedCharOut =
      List.of(msg((int) 'a'), Tick.get(), msg((int) '-'), Tick.get(), msg((int) '0'));

    List<Message<Boolean>> boolInput       = List.of(msg(true), Tick.get(), msg(false), Tick.get(), msg(false));
    List<Message<Boolean>> expectedBoolOut = List.of(msg(false), Tick.get(), msg(true), Tick.get(), msg(true));

    // Given
    CalculationsComp sut = new CalculationsCompBuilder().setName("sut").build();
    sut.port_outByte().connect(this.port_byte);
    sut.port_outShort().connect(this.port_short);
    sut.port_outInt().connect(this.port_int);
    sut.port_outLong().connect(this.port_long);
    sut.port_outFloat().connect(this.port_float);
    sut.port_outDouble().connect(this.port_double);
    sut.port_outChar().connect(this.port_char);
    sut.port_outBoolean().connect(this.port_boolean);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_byte).receive(this.actualByte.capture());
    Mockito.doNothing().when(this.port_short).receive(this.actualShort.capture());
    Mockito.doNothing().when(this.port_int).receive(this.actualInt.capture());
    Mockito.doNothing().when(this.port_long).receive(this.actualLong.capture());
    Mockito.doNothing().when(this.port_float).receive(this.actualFloat.capture());
    Mockito.doNothing().when(this.port_double).receive(this.actualDouble.capture());
    Mockito.doNothing().when(this.port_char).receive(this.actualChar.capture());
    Mockito.doNothing().when(this.port_boolean).receive(this.actualBoolean.capture());

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

    // Then
    Assertions.assertAll(
      () -> assertThat(this.actualByte.getAllValues()).as("bytes").containsExactlyElementsOf(expectedByteOut),
      () -> assertThat(this.actualShort.getAllValues()).as("shorts").containsExactlyElementsOf(expectedShortOut),
      () -> assertThat(this.actualInt.getAllValues()).as("ints").containsExactlyElementsOf(expectedIntOutput),
      () -> assertThat(this.actualLong.getAllValues()).as("longs").containsExactlyElementsOf(expectedLongOutput),
      () -> assertThat(this.actualFloat.getAllValues()).as("floats").containsExactlyElementsOf(expectedFloatOutput),
      () -> assertThat(this.actualDouble.getAllValues()).as("doubles").containsExactlyElementsOf(expectedDoubleOutput),
      () -> assertThat(this.actualChar.getAllValues()).as("chars").containsExactlyElementsOf(expectedCharOut),
      () -> assertThat(this.actualBoolean.getAllValues()).as("bools").containsExactlyElementsOf(expectedBoolOut)
    );
  }
}
