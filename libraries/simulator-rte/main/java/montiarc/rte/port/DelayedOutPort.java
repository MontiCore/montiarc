/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.msg.Message;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * An outgoing port of a MontiArc component that can send ticks and introduce delay.
 * The amount of delay is quantified in ticks.
 *
 * @param <T> the type that can be sent via this port
 */
public class DelayedOutPort<T> extends TimeAwareOutPort<T> {

  Deque<List<T>> buffer = new ArrayDeque<>();

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
    if (delay < 1) {
      throw new IllegalArgumentException("A delayed output port must have a delay of at least 1.");
    }
    for (int i = 0; i < delay; i++) {
      buffer.add(new ArrayList<>());
    }
  }

  @Override
  public void send(T data) {
    send(new Message<>(data));
  }

  @Override
  public void send(Message<T> message) {
    buffer.getLast().add(message.getData());
  }

  @Override
  public void sendTick() {
    super.doTick();

    if (buffer.isEmpty()) { // only here for null-safety
      buffer.add(new ArrayList<>());
      return;
    }

    for (T data : buffer.poll()) {
      super.doSendMessage(data);
    }

    buffer.add(new ArrayList<>());
  }

  /**
   * Send a message without any delay.
   * This is intended to be used for initial values.
   */
  public void sendWithoutDelay(Message<T> message) {
    super.doSendMessage(message);
  }

  /**
   * Send the given data as a message without any delay.
   * This is intended to be used for initial values.
   */
  public void sendWithoutDelay(T data) {
    sendWithoutDelay(new Message<>(data));
  }
}
