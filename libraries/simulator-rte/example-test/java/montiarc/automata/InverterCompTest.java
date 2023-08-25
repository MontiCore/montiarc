/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.msg.Tick;
import montiarc.rte.port.AbstractInPort;
import montiarc.rte.port.ITimeAwareInPort;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;
import montiarc.rte.msg.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

class InverterCompTest {

  TimeAwareOutPort<Boolean> input;
  InverterComp inverterComp;
  Message<Boolean> lastOutMessage;
  TimeAwareInPort<Boolean> outRecipient;

  final static Message<Boolean> NO_MSG = new Message<>(null);

  @BeforeEach
  public void beforeEach() {
    ITimedComponent mockComponent = new ITimedComponent() {
      @Override
      public List<ITimeAwareInPort<?>> getAllInPorts() {
        return null;
      }
      
      @Override
      public List<TimeAwareOutPort<?>> getAllOutPorts() {
        return null;
      }
      
      @Override
      public String getName() {
        return "MockComponent";
      }
      
      @Override
      public void handleMessage(AbstractInPort<?> receivingPort) {
        if(receivingPort == outRecipient) {
          if(outRecipient.isBufferEmpty()) lastOutMessage = null;
          else lastOutMessage = outRecipient.pollBuffer();
        }
      }
    };
    input = new TimeAwareOutPort<>("valueSource", mockComponent);
    inverterComp = new InverterComp("inverter");
    input.connect(inverterComp.port_i);

    lastOutMessage = null;

    outRecipient = new TimeAwareInPort<>("gtEq0Recipient", mockComponent);
    inverterComp.port_o.connect(outRecipient);
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
      Assertions.assertSame(Tick.get(), lastOutMessage);
    }
  }

  public static Stream<Arguments> valueSourceCorrectOutputs() {
    return Stream.of(
      Arguments.of(
        List.of(),
        List.of()
      ),
      Arguments.of(
        List.of(true, true, false, false, true),
        List.of(message(false), message(false), message(true), message(true), message(false))
      )
    );
  }

  protected static <T> Message<T> message(T value) {
    return new Message<>(value);
  }
}
