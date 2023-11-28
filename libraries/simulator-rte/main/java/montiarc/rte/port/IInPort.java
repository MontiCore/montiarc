/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.msg.Message;

public interface IInPort<T> extends IPort {

  /**
   * Receive a message on this port.
   *
   * @param message the received message
   */
  void receive(Message<? extends T> message);
  
  /**
   * Peek the next message in the buffer.
   *
   * @return the next message in the buffer
   */
  Message<T> peekBuffer();
  
  /**
   * Retrieve and remove the next message in the buffer.
   *
   * @return the next message in the buffer
   */
  Message<T> pollBuffer();
  
  /**
   * Check whether the buffer is empty.
   *
   * @return true if the buffer is empty
   */
  boolean isBufferEmpty();
  
  /**
   * Check whether the buffer holds at least one tick.
   *
   * @return true if the buffer contains a tick
   */
  boolean hasBufferedTick();
}
