/* (c) https://github.com/MontiCore/monticore */
package montiarc.decomposed;

import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;
import montiarc.rte.port.messages.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.port.messages.Message.tick;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class DelayedDecomposedExampleTest {
  
  TimeAwareOutPort<Integer> input;
  DelayedDecomposed delayedDecomposed;
  List<Message<Integer>> gtEq0Messages;
  List<Message<Integer>> lt0Messages;
  TimeAwareInPort<Integer> gtEqRecipient;
  TimeAwareInPort<Integer> lt0Recipient;
  
  @BeforeEach
  public void beforeEach() {
    input = new TimeAwareOutPort<>("input");
    delayedDecomposed = new DelayedDecomposed("delayedDecomposed");
    input.connectTo(delayedDecomposed.iIn);
    
    gtEq0Messages = new ArrayList<>();
    lt0Messages = new ArrayList<>();
  
    gtEqRecipient = new TimeAwareInPort<>("gtEq0Recipient") {
      @Override
      protected void handleBuffer() {
        while(!buffer.isEmpty()) {
          gtEq0Messages.add(buffer.poll());
        }
      }
    };
    delayedDecomposed.gtEq0.count.connectTo(gtEqRecipient);
  
    lt0Recipient = new TimeAwareInPort<>("lt0Recipient") {
      @Override
      protected void handleBuffer() {
        while(!buffer.isEmpty()) {
          lt0Messages.add(buffer.poll());
        }
      }
    };
    delayedDecomposed.lt0.count.connectTo(lt0Recipient);
  }

  @ParameterizedTest
  @MethodSource("valueSourceCorrectCountValues")
  public void testCorrectCountValues(List<Integer> inputs,
                                     List<Integer> gtEq0Counts,
                                     List<Integer> lt0Counts) {
    for (int i = 0; i < inputs.size(); i++) {
      // send data
      input.sendMessage(inputs.get(i));
      
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
        arguments(
            List.of(),
            List.of(0),
            List.of(0)
        ),
        arguments(
            List.of(1, 2, 3, 4, 5),
            List.of(0, 1, 2, 3, 4, 5),
            List.of(0, 0 ,0, 0, 0, 0)
        ),
        arguments(
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
      if(message.equals(tick)) {
        input.sendTick();
      } else {
        input.sendMessage(message);
      }
      
      Assertions.assertIterableEquals(gtEq0Outputs.get(i), gtEq0Messages);
      Assertions.assertIterableEquals(lt0Outputs.get(i), lt0Messages);
      gtEq0Messages.clear();
      lt0Messages.clear();
    }
  }
  
  public static Stream<Arguments> valueSourceCorrectOutputs() {
    return Stream.of(
        arguments(
            List.of(),
            List.of(),
            List.of()
        ),
        arguments(
            List.of(tick),
            List.of(
                List.of(tick)
            ),
            List.of(
                List.of(tick)
            )
        ),
        arguments(
            List.of(
                message(100), tick
            ),
            List.of(
                List.of(), List.of(tick, message(1))
            ),
            List.of(
                List.of(), List.of(tick)
            )
        ),
        arguments(
            List.of(
                message(100), message(-100), tick
            ),
            List.of(
                List.of(), List.of(), List.of(tick, message(1))
            ),
            List.of(
                List.of(), List.of(), List.of(tick, message(1))
            )
        ),
        arguments(
            List.of(
                message(100), message(-100), tick,
                message(200), message(-200), message(300), tick
            ),
            List.of(
                List.of(), List.of(), List.of(tick, message(1)),
                List.of(), List.of(), List.of(), List.of(tick, message(2), message(3))
            ),
            List.of(
                List.of(), List.of(), List.of(tick, message(1)),
                List.of(), List.of(), List.of(), List.of(tick, message(2))
            )
        )
    );
  }
  
  
  protected static <T> Message<T> message(T value) {
    return new Message<>(value);
  }
}