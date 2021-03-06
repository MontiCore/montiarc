/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.automaton.TransitionPath;
import sim.message.Message;
import sim.message.TickedMessage;

/**
 * An incoming port is used to receive messages of type T asynchronously.
 *
 * @param <T> data type of this port
 */
public interface IInPort<T> {

  /**
   * Accepts the given data message of type T.
   *
   * @param data message to accept
   */
  void accept(T data);

  /**
   * Accepts the given message on this port.
   *
   * @param message message to accept
   */
  void accept(TickedMessage<? extends T> message);

  /**
   * Accepts a symbolic message
   *
   * @param message symbolic messageto accept
   */
  void symbolicAccept(Message<TransitionPath> message);
}
