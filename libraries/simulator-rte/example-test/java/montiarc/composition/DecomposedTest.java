/* (c) https://github.com/MontiCore/monticore */
package montiarc.composition;

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

public class DecomposedTest {

  TimeAwareOutPort<Integer> input;
  Decomposed decomposed;
  Message<Integer> lastGtEq0Message;
  Message<Integer> lastLt0Message;
  TimeAwareInPort<Integer> gtEq0Recipient;
  TimeAwareInPort<Integer> lt0Recipient;

  final static Message<Integer> NO_MSG = new Message<>(null);

  @BeforeEach
  public void beforeEach() {
    input = new TimeAwareOutPort<>("valueSource");
    decomposed = new Decomposed("decomposed");
    input.connect(decomposed.iIn);

    lastGtEq0Message = null;
    lastLt0Message = null;

    gtEq0Recipient = new TimeAwareInPort<>("gtEq0Recipient") {
      @Override
      protected void handleBuffer() {
        if (buffer.isEmpty()) {
          lastGtEq0Message = null;
        } else {
          lastGtEq0Message = buffer.poll();
        }
      }
    };
    decomposed.gtEq0.count.connect(gtEq0Recipient);

    lt0Recipient = new TimeAwareInPort<>("lt0Recipient") {
      @Override
      protected void handleBuffer() {
        if (buffer.isEmpty()) {
          lastLt0Message = null;
        } else {
          lastLt0Message = buffer.poll();
        }
      }
    };
    decomposed.lt0.count.connect(lt0Recipient);
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
      Assertions.assertEquals(gtEq0Counts.get(i), decomposed.gtEq0.cnt);
      Assertions.assertEquals(lt0Counts.get(i), decomposed.lt0.cnt);
    }
  }

  public static Stream<Arguments> valueSourceCorrectCountValues() {
    return Stream.of(
      Arguments.of(
        List.of(),
        List.of(),
        List.of()
      ),
      Arguments.of(
        List.of(1, 2, 3, 4, 5),
        List.of(1, 2, 3, 4, 5),
        List.of(0, 0, 0, 0, 0)
      ),
      Arguments.of(
        List.of(-1, 1, -2, 2, -3, 3),
        List.of(0, 1, 1, 2, 2, 3),
        List.of(1, 1, 2, 2, 3, 3)
      )
    );
  }

  @ParameterizedTest
  @MethodSource("valueSourceCorrectOutputs")
  public void testCorrectOutputs(List<Integer> inputs,
                                 List<Message<Integer>> gtEq0Outputs,
                                 List<Message<Integer>> lt0Outputs) {
    for (int i = 0; i < inputs.size(); i++) {
      // set up iteration
      lastGtEq0Message = NO_MSG;
      lastLt0Message = NO_MSG;

      // do step 1: send data
      input.send(inputs.get(i));

      // check results of step 1: correct outputs
      Assertions.assertEquals(gtEq0Outputs.get(i), lastGtEq0Message);
      Assertions.assertEquals(lt0Outputs.get(i), lastLt0Message);

      // do step 2: send tick
      input.sendTick();

      // check results of step 2: both outputs propagated the tick
      Assertions.assertSame(Message.tick, lastGtEq0Message);
      Assertions.assertSame(Message.tick, lastLt0Message);
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
        List.of(1, 2, 3, 4, 5),
        List.of(message(1), message(2), message(3), message(4), message(5)),
        List.of(NO_MSG, NO_MSG, NO_MSG, NO_MSG, NO_MSG)
      ),
      Arguments.of(
        List.of(-1, 1, -2, 2, -3, 3),
        List.of(NO_MSG, message(1), NO_MSG, message(2), NO_MSG, message(3)),
        List.of(message(1), NO_MSG, message(2), NO_MSG, message(3), NO_MSG)
      )
    );
  }

  protected static <T> Message<T> message(T value) {
    return new Message<>(value);
  }
}
