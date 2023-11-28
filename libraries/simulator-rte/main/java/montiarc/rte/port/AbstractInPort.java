/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.IComponent;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * An incoming port of a MontiArc component.
 * Connections between ports are implemented via the observer pattern.
 * In terms of the observer pattern, incoming ports are observers of outgoing ports.
 * <br>
 * Since this class represents incoming ports in MontiArc, it can only be registered to one sender at a time.
 * <br>
 * Incoming messages are stored in a FIFO buffer
 *
 * @param <T> the type that can be received via this port
 */
public abstract class AbstractInPort<T> extends AbstractBasePort<T> implements IInPort<T> {

  protected Queue<Message<T>> buffer = new ArrayDeque<>();

  protected AbstractInPort(String qualifiedName, IComponent owner) {
    super(qualifiedName, owner);
  }

  /**
   * Receive a message on this port.
   * This method should only be called by the {@link AbstractOutPort}
   * to which this incoming port is registered.
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
      handleBuffer();
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
      handleBuffer();
    }
  }

  /**
   * Call the owning component's behavior associated with messages on this port.
   * {@link IComponent#handleMessage(IInPort)} should then also handle polling of used values form the buffer.
   */
  protected final void handleBuffer() {
    owner.handleMessage(this);
  }

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
   * Retrieve and remove the next message in the buffer.
   *
   * @return the next message in the buffer
   */
  @Override
  public Message<T> pollBuffer() {
    return buffer.poll();
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
