/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.port.messages.Message;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * An outgoing port of a MontiArc component that can send ticks and introduce delay.
 * The amount of delay is quantified in ticks.
 *
 * @param <DataType> the type that can be sent via this port
 */
public class DelayedOutPort<DataType> extends TimeAwareOutPort<DataType> {
  
  Deque<List<DataType>> buffer = new ArrayDeque<>();
  
  /**
   * Create a new outgoing port with the default delay of 1 tick.
   */
  public DelayedOutPort(String qualifiedName) {
    this(qualifiedName, 1);
  }
  
  /**
   * Create a new outgoing port with a custom amount of delay (>=1 tick).
   */
  public DelayedOutPort(String qualifiedName, int delay) {
    super(qualifiedName);
    if(delay < 1) {
      throw new IllegalArgumentException("A delayed output port must have a delay of at least 1.");
    }
    for(int i = 0; i < delay; i++) {
      buffer.add(new ArrayList<>());
    }
  }
  
  @Override
  public void sendMessage(DataType data) {
    sendMessage(new Message<>(data));
  }
  
  @Override
  public void sendMessage(Message<DataType> message) {
    buffer.getLast().add(message.getValue());
  }
  
  @Override
  public void sendTick() {
    super.doTick();
    
    if(buffer.isEmpty()) { // only here for null-safety
      buffer.add(new ArrayList<>());
      return;
    }
    
    for(DataType data: buffer.poll()) {
      super.doSendMessage(data);
    }
    
    buffer.add(new ArrayList<>());
  }
  
  /**
   * Send a message without any delay.
   * This is intended to be used for initial values.
   */
  public void sendWithoutDelay(Message<DataType> message) {
    super.doSendMessage(message);
  }
  
  /**
   * Send the given data as a message without any delay.
   * This is intended to be used for initial values.
   */
  public void sendWithoutDelay(DataType data) {
    sendWithoutDelay(new Message<>(data));
  }
}
