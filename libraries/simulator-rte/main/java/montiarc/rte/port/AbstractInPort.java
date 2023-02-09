/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.port.messages.Message;

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
 * @param <DataType> the type that can be received via this port
 */
public abstract class AbstractInPort<DataType> extends AbstractBasePort<DataType> implements IInPort<DataType> {
  
  protected Queue<Message<DataType>> buffer = new ArrayDeque<>();
  
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
  public void receiveMessage(Message<DataType> message) {
    if(messageIsValidOnPort(message)) {
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
  public Message<DataType> peekBuffer() {
    return buffer.peek();
  }
  
  /**
   * Retrieve and remove the next message in the buffer.
   *
   * @return the next message in the buffer
   */
  public Message<DataType> pollBuffer() {
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
