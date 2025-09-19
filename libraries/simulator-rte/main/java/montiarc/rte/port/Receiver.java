/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.msg.Message;

/**
 * Interface for anything that can receive {@link Message}s.
 * E.g. Ports.
 *
 * @param <T>
 */
public interface Receiver<T> {

  /**
   * Receive a message on this port.
   *
   * @param message the received message
   */
  void receive(Message<? extends T> message);
}
