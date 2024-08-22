/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;

import java.util.Objects;

/**
 * A port that can receive messages and thereby is the target of connectors.
 *
 * @param <T> the message's data type
 */
public interface InPort<T> extends Port {

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

  Message<T> peekLastBuffer();

  Message<T> pollLastBuffer();

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

  /**
   * Whether this port is currently blocked by a tick, i.e., it is not tickfree (see Haber Diss p.96).
   *
   * @return true if the next buffered message is a tick
   */
  default boolean isTickBlocked() {
    return Objects.equals(this.peekBuffer(), Tick.get());
  }

  /**
   * Remove the next buffered message if it is a tick
   */
  default void dropBlockingTick() {
    if (this.isTickBlocked()) {
      this.pollBuffer();
    }
  }

  /**
   * Drop all messages except for the last one that is queued before the next tick.
   * If there is no tick queued, all messages except for the last one are dropped.
   */
  void dropMessagesIgnoredBySync();
}
