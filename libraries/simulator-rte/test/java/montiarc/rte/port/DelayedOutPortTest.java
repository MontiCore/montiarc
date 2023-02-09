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

import static montiarc.rte.port.messages.Message.tick;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class DelayedOutPortTest {
  
  DelayedOutPort<String> portUnderTest;
  TimeAwareInPort<String> recipient;
  List<Message<String>> receivedMessages;
  
  void setUpPorts(int delay) {
    receivedMessages = new ArrayList<>();
    portUnderTest = new DelayedOutPort<>("portUnderTest", delay);
    recipient = new TimeAwareInPort<>("recipient") {
      @Override
      protected void handleBuffer() {
        while(!buffer.isEmpty()) {
          receivedMessages.add(buffer.poll());
        }
      }
    };
    
    portUnderTest.connectTo(recipient);
  }
  
  @ParameterizedTest
  @MethodSource("valueSourceDelayedPort")
  void testDelayedPort(int delay, List<Message<String>> inputs, List<List<Message<String>>> outputs) {
    // Given
    setUpPorts(delay);
    
    for (int i = 0; i < inputs.size(); i++) {
      // When
      Message<String> message = inputs.get(i);
      if(message.equals(tick)) {
        portUnderTest.sendTick();
      } else {
        portUnderTest.sendMessage(message);
      }
  
      // Then
      Assertions.assertIterableEquals(outputs.get(i), receivedMessages);
      receivedMessages.clear();
    }
  }
  
  public static Stream<Arguments> valueSourceDelayedPort() {
    return Stream.of(
        arguments(
            1,
            List.of(),
            List.of()
        ),
        arguments(
            1,
            List.of(
                message("A"), tick,
                message("B"), message("C"), tick,
                message("D"), tick,
                tick,
                tick
            ),
            List.of(
                List.of(), List.of(tick, message("A")),
                List.of(), List.of(), List.of(tick, message("B"), message("C")),
                List.of(), List.of(tick, message("D")),
                List.of(tick),
                List.of(tick)
            )
        ),
        arguments(
            2,
            List.of(
                message("A"), tick,
                message("B"), tick,
                message("C"), message("D"), tick,
                tick,
                tick
            ),
            List.of(
                List.of(), List.of(tick),
                List.of(), List.of(tick, message("A")),
                List.of(), List.of(), List.of(tick, message("B")),
                List.of(tick, message("C"), message("D")),
                List.of(tick)
            )
        )
    );
  }
  
  protected static <T> Message<T> message(T value) {
    return new Message<>(value);
  }
}