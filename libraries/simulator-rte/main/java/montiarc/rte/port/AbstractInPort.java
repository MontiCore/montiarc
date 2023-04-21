/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.msg.Message;

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

  protected AbstractInPort(String qualifiedName) {
    super(qualifiedName);
  }

  /**
   * Receive a message on this port.
   * This method should only be called by the {@link AbstractOutPort}
   * to which this incoming port is registered.
   *
   * @param message the message sent by the connected outgoing port
   */
  @Override
  public void receive(Message<T> message) {
    if (messageIsValidOnPort(message)) {
      buffer.add(message);
      handleBuffer();
    }
  }

  /**
   * This method should call the owning component's behavior associated with messages on this port.
   * It is intended to be implemented for each port instance individually.
   */
  protected abstract void handleBuffer();

  /**
   * Peek the next message in the buffer.
   *
   * @return the next message in the buffer
   */
  public Message<T> peekBuffer() {
    return buffer.peek();
  }

  /**
   * Retrieve and remove the next message in the buffer.
   *
   * @return the next message in the buffer
   */
  public Message<T> pollBuffer() {
    return buffer.poll();
  }

  /**
   * Check whether the buffer is empty.
   *
   * @return true if the buffer is empty
   */
  public boolean isBufferEmpty() {
    return buffer.isEmpty();
  }
}
