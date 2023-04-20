/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.port.messages.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DelayedOutPortTest {

  DelayedOutPort<String> portUnderTest;
  TimeAwareInPort<String> recipient;
  List<Message<String>> receivedMessages;

  void setUpPorts(int delay) {
    receivedMessages = new ArrayList<>();
    portUnderTest = new DelayedOutPort<>("portUnderTest", delay);
    recipient = new TimeAwareInPort<>("recipient") {
      @Override
      protected void handleBuffer() {
        while (!buffer.isEmpty()) {
          receivedMessages.add(buffer.poll());
        }
      }
    };

    portUnderTest.connect(recipient);
  }

  @ParameterizedTest
  @MethodSource("valueSourceDelayedPort")
  void testDelayedPort(int delay, List<Message<String>> inputs, List<List<Message<String>>> outputs) {
    // Given
    setUpPorts(delay);

    for (int i = 0; i < inputs.size(); i++) {
      // When
      Message<String> message = inputs.get(i);
      if (message.equals(Message.tick)) {
        portUnderTest.sendTick();
      } else {
        portUnderTest.send(message);
      }

      // Then
      Assertions.assertIterableEquals(outputs.get(i), receivedMessages);
      receivedMessages.clear();
    }
  }

  public static Stream<Arguments> valueSourceDelayedPort() {
    return Stream.of(
      Arguments.of(
        1,
        List.of(),
        List.of()
      ),
      Arguments.of(
        1,
        List.of(
          message("A"), Message.tick,
          message("B"), message("C"), Message.tick,
          message("D"), Message.tick,
          Message.tick,
          Message.tick
        ),
        List.of(
          List.of(), List.of(Message.tick, message("A")),
          List.of(), List.of(), List.of(Message.tick, message("B"), message("C")),
          List.of(), List.of(Message.tick, message("D")),
          List.of(Message.tick),
          List.of(Message.tick)
        )
      ),
      Arguments.of(
        2,
        List.of(
          message("A"), Message.tick,
          message("B"), Message.tick,
          message("C"), message("D"), Message.tick,
          Message.tick,
          Message.tick
        ),
        List.of(
          List.of(), List.of(Message.tick),
          List.of(), List.of(Message.tick, message("A")),
          List.of(), List.of(), List.of(Message.tick, message("B")),
          List.of(Message.tick, message("C"), message("D")),
          List.of(Message.tick)
        )
      )
    );
  }

  protected static <T> Message<T> message(T value) {
    return new Message<>(value);
  }
}
