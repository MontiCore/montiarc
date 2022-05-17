/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.message.Message;
import sim.message.TickedMessage;
import sim.Automaton.Transitionpath;

/**
 * An outgoing port is used to send messages to connected receivers.
 *
 * @param <T> data type of this port
 */
public interface IOutPort<T> {

  /**
   * Sends the given {@link TickedMessage} to all receiving ports.
   *
   * @param message message to send
   */
  void send(TickedMessage<T> message);

  /**
   * Accepts a symbolic message
   *
   * @param message symbolic messageto accept
   */
  void symbolicSend(Message<Transitionpath> message);
}