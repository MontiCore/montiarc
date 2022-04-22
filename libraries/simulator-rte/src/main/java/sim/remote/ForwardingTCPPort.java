/* (c) https://github.com/MontiCore/monticore */
package sim.remote;

import sim.message.Message;
import sim.Automaton.Transitionpath;
import sim.port.ForwardPort;

/**
 * Interface for a remote forwarding incoming tcp port.
 *
 * @param <T> communication data type
 */
public class ForwardingTCPPort<T> extends ForwardPort<T> implements IInTCPPort<T> {

  public void startListenOn(int tcpPort) {
    new Thread(new InPortTCPServer<T>(this, tcpPort)).start();
  }

  @Override
  public void symbolicSend(Message<Transitionpath> message) {

  }
}
