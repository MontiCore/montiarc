/**
 * 
 */
package sim.port;

/**
 * Port facotry for master slave ports.
 *
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Date: 2013-04-09 16:37:35 +0200 (Di, 09 Apr 2013) $<br>
 *          $Revision: 2169 $
 */
public class MasterSlavePortFactory implements IPortFactory {

    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createInPort()
     */
    @Override
    public <T> IInSimPort<T> createInPort() {
        return new MasterSlavePort<T>();
    }

    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createOutPort()
     */
    @Override
    public <T> IOutSimPort<T> createOutPort() {
        return new MasterSlavePort<T>();
    }

    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createForwardPort()
     */
    @Override
    public <T> IForwardPort<T> createForwardPort() {
        return new ForwardPort<T>();
    }
    
}
