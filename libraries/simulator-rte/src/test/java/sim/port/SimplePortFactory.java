/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.port;


/**
 * Factory that produces {@link SimplePort}s.
 *
 *
 * 
 */
public class SimplePortFactory implements IPortFactory {
    
    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createIncomingPort()
     */
    @Override
    public <T> IInSimPort<T> createInPort() {
        return new SimplePort<T>();
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
        return new SimplePort<T>();
    }
    
}
