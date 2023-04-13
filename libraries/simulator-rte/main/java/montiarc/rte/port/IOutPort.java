/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.port.messages.Message;

public interface IOutPort<T> {

  /**
   * Connects to the given port, registering it as a recipient.
   *
   * @param recipient the port to connect to
   * @return true iff the ports connect successfully
   */
  boolean connect(IInPort<T> recipient);

  /**
   * Disconnects from the given port, removing it as a recipient.
   *
   * @param recipient the port to disconnect from
   * @return true if the ports disconnect successfully or if the ports where not connected in the first place
   */
  boolean disconnect(IInPort<T> recipient);

  /**
   * Send out the given data as a message to all registered recipients.
   * <br>
   * Do not use this method to send ticks. Instead, use {@link #sendTick()}.
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
  void send(Message<T> message);

  /**
   * Try to send a tick via this port.
   */
  void sendTick();

}
