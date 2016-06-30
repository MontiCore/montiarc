/**
 * 
 */
package sim.port;


/**
 * Factory that produces {@link SimplePort}s.
 *
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $LastChangedBy: ahaber $
 * @version $LastChangedDate: 2013-06-05 17:33:36 +0200 (Mi, 05 Jun 2013) $<br>
 *          $LastChangedRevision: 2376 $
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
