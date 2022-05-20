/* (c) https://github.com/MontiCore/monticore */
package sim.remote;

import sim.port.IForwardPort;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;
import sim.port.IPortFactory;

/**
 * Produces remote tcp ports.
 */
public class TCPPortFactory implements IPortFactory {

  /**
   * @see sim.port.IPortFactory#createInPort()
   */
  @Override
  public <T> IInSimPort<T> createInPort() {
    return new TCPPort<T>();
  }

  /**
   * @see sim.port.IPortFactory#createOutPort()
   */
  @Override
  public <T> IOutSimPort<T> createOutPort() {
    return new TCPPort<T>();
  }

  /**
   * @see sim.port.IPortFactory#createForwardPort()
   */
  @Override
  public <T> IForwardPort<T> createForwardPort() {
    return new ForwardingTCPPort<T>();
  }
}
