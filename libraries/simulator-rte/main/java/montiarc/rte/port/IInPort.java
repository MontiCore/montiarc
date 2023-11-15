/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.msg.Message;

public interface IInPort<T> {

  /**
   * Receive a message on this port.
   *    
   * @param message the received message
   */
  void receive(Message<? extends T> message);
}
