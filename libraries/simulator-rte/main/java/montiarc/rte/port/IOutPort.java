/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.port.messages.Message;

public interface IOutPort<DataType> {
  
  /**
   * Connect the given incoming port to this outgoing port.
   *
   * @param recipient the port that should be connected to this port
   *
   * @return true iff the given port is connected to this port when this method finishes
   */
  boolean connectTo(IInPort<DataType> recipient);
  
  /**
   * Disconnect the given incoming port from this outgoing port.
   *
   * @param recipient the port that should be disconnected from this port
   *
   * @return true iff the given port is not connected to this port when this method finishes
   */
  boolean disconnect(IInPort<DataType> recipient);
  
  /**
   * Send out the given data as a message to all registered recipients.
   * <br>
   * Do not use this method to send ticks. Instead, use {@link #sendTick()}.
   *
   * @param data the data to send
   */
  void sendMessage(DataType data);
  
  /**
   * Send out the given message to all registered recipients.
   * <br>
   * Do not use this method to send ticks. Instead, use {@link #sendTick()}.
   *
   * @param message the message to send
   */
  void sendMessage(Message<DataType> message);
  
  /**
   * Try to send a tick via this port.
   */
  void sendTick();
}
