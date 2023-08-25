/* (c) https://github.com/MontiCore/monticore */
package montiarc.composition;

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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DelayedDecomposedTest {

  TimeAwareOutPort<Integer> input;
  DelayedDecomposed delayedDecomposed;
  List<Message<Integer>> gtEq0Messages;
  List<Message<Integer>> lt0Messages;
  TimeAwareInPort<Integer> gtEqRecipient;
  TimeAwareInPort<Integer> lt0Recipient;

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
        if(receivingPort == gtEqRecipient) {
          while(!gtEqRecipient.isBufferEmpty()) {
            gtEq0Messages.add(gtEqRecipient.pollBuffer());
          }
        } else if(receivingPort == lt0Recipient) {
          while(!lt0Recipient.isBufferEmpty()) {
            lt0Messages.add(lt0Recipient.pollBuffer());
          }
        }
      }
    };
    input = new TimeAwareOutPort<>("input", mockComponent);
    delayedDecomposed = new DelayedDecomposed("delayedDecomposed");
    input.connect(delayedDecomposed.iIn);

    gtEq0Messages = new ArrayList<>();
    lt0Messages = new ArrayList<>();

    gtEqRecipient = new TimeAwareInPort<>("gtEq0Recipient", mockComponent);
    delayedDecomposed.gtEq0.count.connect(gtEqRecipient);

    lt0Recipient = new TimeAwareInPort<>("lt0Recipient", mockComponent);
    delayedDecomposed.lt0.count.connect(lt0Recipient);
  }

  @ParameterizedTest
  @MethodSource("valueSourceCorrectCountValues")
  public void testCorrectCountValues(List<Integer> inputs,
                                     List<Integer> gtEq0Counts,
                                     List<Integer> lt0Counts) {
    for (int i = 0; i < inputs.size(); i++) {
      // send data
      input.send(inputs.get(i));

      // check results: correct counts in both components
      Assertions.assertEquals(gtEq0Counts.get(i), delayedDecomposed.gtEq0.cnt);
      Assertions.assertEquals(lt0Counts.get(i), delayedDecomposed.lt0.cnt);

      // send tick
      input.sendTick();
    }

    // send one more tick to get the last value from the delayed port
    input.sendTick();

    Assertions.assertEquals(gtEq0Counts.get(gtEq0Counts.size() - 1), delayedDecomposed.gtEq0.cnt);
    Assertions.assertEquals(lt0Counts.get(lt0Counts.size() - 1), delayedDecomposed.lt0.cnt);
  }

  public static Stream<Arguments> valueSourceCorrectCountValues() {
    return Stream.of(
      Arguments.of(
        List.of(),
        List.of(0),
        List.of(0)
      ),
      Arguments.of(
        List.of(1, 2, 3, 4, 5),
        List.of(0, 1, 2, 3, 4, 5),
        List.of(0, 0, 0, 0, 0, 0)
      ),
      Arguments.of(
        List.of(-1, 1, -2, 2, -3, 3),
        List.of(0, 0, 1, 1, 2, 2, 3),
        List.of(0, 1, 1, 2, 2, 3, 3)
      )
    );
  }

  @ParameterizedTest
  @MethodSource("valueSourceCorrectOutputs")
  public void testCorrectOutputs(List<Message<Integer>> inputs,
                                 List<List<Message<Integer>>> gtEq0Outputs,
                                 List<List<Message<Integer>>> lt0Outputs) {
    for (int i = 0; i < inputs.size(); i++) {
      Message<Integer> message = inputs.get(i);
      if (message.equals(Tick.get())) {
        input.sendTick();
      } else {
        input.send(message);
      }

      Assertions.assertIterableEquals(gtEq0Outputs.get(i), gtEq0Messages);
      Assertions.assertIterableEquals(lt0Outputs.get(i), lt0Messages);
      gtEq0Messages.clear();
      lt0Messages.clear();
    }
  }

  public static Stream<Arguments> valueSourceCorrectOutputs() {
    return Stream.of(
      Arguments.of(
        List.of(),
        List.of(),
        List.of()
      ),
      Arguments.of(
        List.of(Tick.get()),
        List.of(
          List.of(Tick.get())
        ),
        List.of(
          List.of(Tick.get())
        )
      ),
      Arguments.of(
        List.of(
          message(100), Tick.get()
        ),
        List.of(
          List.of(), List.of(Tick.get(), message(1))
        ),
        List.of(
          List.of(), List.of(Tick.get())
        )
      ),
      Arguments.of(
        List.of(
          message(100), message(-100), Tick.get()
        ),
        List.of(
          List.of(), List.of(), List.of(Tick.get(), message(1))
        ),
        List.of(
          List.of(), List.of(), List.of(Tick.get(), message(1))
        )
      ),
      Arguments.of(
        List.of(
          message(100), message(-100), Tick.get(),
          message(200), message(-200), message(300), Tick.get()
        ),
        List.of(
          List.of(), List.of(), List.of(Tick.get(), message(1)),
          List.of(), List.of(), List.of(), List.of(Tick.get(), message(2), message(3))
        ),
        List.of(
          List.of(), List.of(), List.of(Tick.get(), message(1)),
          List.of(), List.of(), List.of(), List.of(Tick.get(), message(2))
        )
      )
    );
  }

  protected static <T> Message<T> message(T value) {
    return new Message<>(value);
  }
}
