/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;
import montiarc.rte.port.messages.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.port.messages.Message.tick;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class InverterTest {

  TimeAwareOutPort<Boolean> input;
  Inverter inverter;
  Message<Boolean> lastOutMessage;
  TimeAwareInPort<Boolean> outRecipient;

  final static Message NO_MSG = new Message(null);

  @BeforeEach
  public void beforeEach() {
    input = new TimeAwareOutPort<>("valueSource");
    inverter = new Inverter("inverter");
    input.connect(inverter.i);

    lastOutMessage = null;

    outRecipient = new TimeAwareInPort<>("gtEq0Recipient") {
      @Override
      protected void handleBuffer() {
        if (buffer.isEmpty()) {
          lastOutMessage = null;
        } else {
          lastOutMessage = buffer.poll();
        }
      }
    };
    inverter.o.connect(outRecipient);
  }

  @ParameterizedTest
  @MethodSource("valueSourceCorrectOutputs")
  public void testCorrectOutputs(List<Boolean> inputs,
                                 List<Message<Boolean>> expectedOutputs) {
    for (int i = 0; i < inputs.size(); i++) {
      // set up iteration
      lastOutMessage = NO_MSG;

      // do step 1: send data
      input.send(inputs.get(i));

      // check results of step 1: correct outputs
      Assertions.assertEquals(expectedOutputs.get(i), lastOutMessage);

      // do step 2: send tick
      input.sendTick();

      // check results of step 2: both outputs propagated the tick
      Assertions.assertSame(tick, lastOutMessage);
    }
  }

  public static Stream<Arguments> valueSourceCorrectOutputs() {
    return Stream.of(
      arguments(
        List.of(),
        List.of()
      ),
      arguments(
        List.of(true, true, false, false, true),
        List.of(message(false), message(false), message(true), message(true), message(false))
      )
    );
  }

  protected static <T> Message<T> message(T value) {
    return new Message<>(value);
  }
}
