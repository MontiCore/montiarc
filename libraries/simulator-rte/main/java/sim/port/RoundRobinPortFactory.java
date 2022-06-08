/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import java.io.Serializable;

/**
 * Factory that produces {@link Port}s.
 */
public class RoundRobinPortFactory implements IPortFactory, Serializable {

  /**
   * @see IPortFactory#createInPort()
   */
  @Override
  public <T> IInSimPort<T> createInPort() {
    return new RoundRobinPort<>();
  }

  /**
   * @see IPortFactory#createForwardPort()
   */
  @Override
  public <T> IForwardPort<T> createForwardPort() {
    return new ForwardPort<>();
  }

  /**
   * @see sim.port.IPortFactory#createOutPort()
   */
  @Override
  public <T> IOutSimPort<T> createOutPort() {
    return new RoundRobinPort<>();
  }
}