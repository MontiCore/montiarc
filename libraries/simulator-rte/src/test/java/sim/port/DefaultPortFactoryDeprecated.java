/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.port;


/**
 * Factory that produces {@link PortDeprecated}. 
 * DefaultPortFactoryDeprecated is for tests only. Use {@link DefaultPortFactory} instead.
 * 
 * Used 
 * 
 */
public class DefaultPortFactoryDeprecated implements IPortFactory {
    
    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createIncomingPort()
     */
    @Override
    public <T> IInSimPort<T> createInPort() {
        return new PortDeprecated<T>();
    }
    
    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createIncomingForwardingPort()
     */
    @Override
    public <T> IForwardPort<T> createForwardPort() {
        return new ForwardPort<T>();
    }

    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createOutPort()
     */
    @Override
    public <T> IOutSimPort<T> createOutPort() {
        return new OutPort<T>();
    }
    
}
