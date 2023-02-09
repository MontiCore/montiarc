/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.port.messages.Message;

public interface IInPort<DataType> {
  
  /**
   * Receive a message on this port.
   * This method should only be called by the {@link IOutPort}
   * to which this incoming port is connected.
   *
   * @param message the message sent by the connected outgoing port
   */
  void receiveMessage(Message<DataType> message);
}
