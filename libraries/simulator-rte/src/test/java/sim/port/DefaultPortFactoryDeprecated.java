/* (c) https://github.com/MontiCore/monticore */
package sim.port;


/**
 * Factory that produces {@link PortDeprecated}. DefaultPortFactoryDeprecated is for tests only. Use {@link
 * DefaultPortFactory} instead.
 * <p>
 * Used
 */
public class DefaultPortFactoryDeprecated implements IPortFactory {

  /**
   * @see IPortFactory#createInPort()
   */
  @Override
  public <T> IInSimPort<T> createInPort() {
    return new PortDeprecated<T>();
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
    return new OutPort<T>();
  }
}
