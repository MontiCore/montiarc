/* (c) https://github.com/MontiCore/monticore */
package sim.port;


/**
 * Factory that produces {@link Port}s.
 */
public class DefaultPortFactory implements IPortFactory {

  /**
   * @see IPortFactory#createInPort()
   */
  @Override
  public <T> IInSimPort<T> createInPort() {
    return new Port<T>();
  }

  /**
   * @see IPortFactory#createForwardPort()
   */
  @Override
  public <T> IForwardPort<T> createForwardPort() {
    return new ForwardPort<T>();
  }

  /**
   * @see sim.port.IPortFactory#createOutPort()
   */
  @Override
  public <T> IOutSimPort<T> createOutPort() {
    return new Port<T>();
  }
}
