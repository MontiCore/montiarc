/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.IComponent;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * This class represents a port-forwarding connector in a MontiArc model (time-aware variant).
 * <br>
 * A port-forwarding connector is either going from a subcomponent to an outgoing port of
 * its owning component or from an incoming port of a component to an incoming port of
 * one of its subcomponents. If one incoming port is connected to multiple subcomponents,
 * the connection can no longer be represented by sharing the port instance, thus this structure is required.
 *
 * @param <T> the type that is sent via this forward
 */
public class TimeAwarePortForward<T> extends TimeAwareOutPort<T> implements ITimeAwareInPort<T> {

  protected Queue<Message<T>> buffer = new ArrayDeque<>();
  
  public TimeAwarePortForward(String qualifiedName, IComponent owner) {
    super(qualifiedName, owner);
  }

  /**
   * Receive a message on this port, which is buffered for forwarding.
   * This method should only be called by the {@link IOutPort}
   * to which this port forward is connected.
   *
   * @param message the message sent by the connected outgoing port
   */
  @Override
  public void receive(Message<? extends T> message) {
    if (message == Tick.get()) {
      this.processReceivedTick();
    } else {
      this.processReceivedData(message.getData());
    }
  }
  
  /**
   * Process a received data message on this port.
   * <br>
   * Do not call this method to <i>send</i> messages to this port, use {@link #receive(Message)} instead:
   * {@code receive(Message.of(data))}.
   *
   * @param data the received data
   */
  protected void processReceivedData(T data) {
    Message<T> msgObject = Message.of(data);
    if (messageIsValidOnPort(msgObject)) {
      buffer.add(msgObject);
      owner.handleMessage(this);
    }
  }
  
  /**
   * Process a received tick message on this port.
   * Do not call this method to <i>send</i> messages to this port, use {@link #receive(Message)} instead:
   * {@code receive(Tick.get())}.
   */
  protected void processReceivedTick() {
    if (messageIsValidOnPort(Tick.get())) {
      buffer.add(Tick.get());
      owner.handleMessage(this);
    }
  }
  
  /**
   * Forward the next message from the buffer.
   */
  public void forward() {
    this.send(buffer.poll());
  }
  
  /**
   * Since port forwards only act when {@link #forward} is called, this does nothing.
   */
  @Override
  public void continueAfterDroppedTick() { }
  
  /**
   * Peek the next message in the buffer.
   *
   * @return the next message in the buffer
   */
  @Override
  public Message<T> peekBuffer() {
    return buffer.peek();
  }
  
  /**
   * Port forwards' buffers should not be polled directly.
   * Therefore, this method throws an exception.
   * Rather, {@link #forward()} should be used to process messages.
   *
   * @return null
   */
  @Override
  public Message<T> pollBuffer() {
    throw new UnsupportedOperationException("Port forward buffer cannot be polled directly. Use 'forward' method.");
  }
  
  /**
   * Check whether the buffer is empty.
   *
   * @return true if the buffer is empty
   */
  @Override
  public boolean isBufferEmpty() {
    return buffer.isEmpty();
  }
  
  /**
   * Check whether the buffer holds at least one tick.
   *
   * @return true if the buffer contains a tick
   */
  @Override
  public boolean hasBufferedTick() {
    return buffer.contains(Tick.get());
  }
}
