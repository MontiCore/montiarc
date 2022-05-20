/* (c) https://github.com/MontiCore/monticore */
package sim.port;

/**
 * Port factory for master slave ports.
 */
public class MasterSlavePortFactory implements IPortFactory {

  /**
   * @see sim.port.IPortFactory#createInPort()
   */
  @Override
  public <T> IInSimPort<T> createInPort() {
    return new MasterSlavePort<T>();
  }

  /**
   * @see sim.port.IPortFactory#createOutPort()
   */
  @Override
  public <T> IOutSimPort<T> createOutPort() {
    return new MasterSlavePort<T>();
  }

  /**
   * @see sim.port.IPortFactory#createForwardPort()
   */
  @Override
  public <T> IForwardPort<T> createForwardPort() {
    return new ForwardPort<T>();
  }
}
