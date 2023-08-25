/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.IComponent;
import montiarc.rte.msg.Message;

/**
 * This class represents a port-forwarding connector in a MontiArc model (time-aware variant).
 * <br>
 * A port-forwarding connector is one going from an incoming port of a component
 * to an incoming port of its subcomponent. If one incoming port is connected to
 * multiple subcomponents, the connection can no longer be represented by
 * sharing the port instance, thus this structure is required.
 *
 * @param <T> the type that is sent via this forward
 */
public class TimeAwarePortForward<T> extends TimeAwareOutPort<T> implements ITimeAwareInPort<T> {

  public TimeAwarePortForward(String qualifiedName, IComponent<?, ?> owner) {
    super(qualifiedName, owner);
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
  
  /**
   * Port forwards are never tick blocked in this implementation.
   *
   * @return false
   */
  @Override
  public boolean isTickBlocked() {
    return false;
  }
  
  /**
   * Since port forwards are never tick blocked, this does nothing.
   */
  @Override
  public void dropBlockingTick() { }
  
  /**
   * Since port forwards are never tick blocked, this does nothing.
   */
  @Override
  public void continueAfterDroppedTick() { }
}
