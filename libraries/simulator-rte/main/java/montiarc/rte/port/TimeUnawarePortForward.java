/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.port.messages.Message;

/**
 * This class represents a port-forwarding connector in a MontiArc model (time-unaware variant).
 * <br>
 * A port-forwarding connector is one going from an incoming port of a component
 * to an incoming port of its subcomponent. If one incoming port is connected to
 * multiple subcomponents, the connection can no longer be represented by
 * sharing the port instance, thus this structure is required.
 *
 * @param <T> the type that is sent via this forward
 */
public class TimeUnawarePortForward<T> extends TimeUnawareOutPort<T> implements IInPort<T> {

  public TimeUnawarePortForward(String qualifiedName) {
    super(qualifiedName);
  }

  /**
   * Receive a message on this port, which is instantly forwarded.
   * This method should only be called by the {@link IOutPort}
   * to which this port forward is connected.
   *
   * @param message the message sent by the connected outgoing port
   */
  @Override
  public void receive(Message<T> message) {
    this.send(message);
  }
}
