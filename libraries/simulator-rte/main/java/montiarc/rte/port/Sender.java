/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.msg.Message;

/**
 * Interface for anything that can send {@link Message}s to {@link Receiver}.
 *
 * @param <T>
 */
public interface Sender<T> {

  /**
   * Send out the given data as a message to all registered recipients.
   *
   * @param data the data to send
   */
  void send(T data);

  /**
   * Send out the given message to all registered recipients.
   * <br>
   * Do not use this method to send ticks. Instead, use {@link #sendTick()}.
   *
   * @param message the message to send
   */
  void send(Message<? extends T> message);

  /**
   * Connects to the given port, registering it as a recipient.
   *
   * @param recipient the port to connect to
   * @return true iff the ports connect successfully
   */
  boolean connect(Receiver<? super T> recipient);

  /**
   * Disconnects from the given port, removing it as a recipient.
   *
   * @param recipient the port to disconnect from
   * @return true if the ports disconnect successfully or if the ports where not connected in the first place
   */
  boolean disconnect(Receiver<? super T> recipient);

  /**
   * Try to send a tick via this port.
   */
  void sendTick();
}
