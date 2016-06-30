/**
 * 
 */
package sim.port;


/**
 * Factory that produces {@link Port}s.
 *
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $LastChangedBy: ahaber $
 * @version $LastChangedDate: 2013-04-05 10:19:04 +0200 (Fr, 05 Apr 2013) $<br>
 *          $LastChangedRevision: 2147 $
 */
public class DefaultPortFactory implements IPortFactory {
    
    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createIncomingPort()
     */
    @Override
    public <T> IInSimPort<T> createInPort() {
        return new Port<T>();
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
        return new Port<T>();
    }
    
}
