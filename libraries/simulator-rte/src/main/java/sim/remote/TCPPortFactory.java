/**
 * 
 */
package sim.remote;

import sim.port.IForwardPort;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;
import sim.port.IPortFactory;

/**
 * Produces remote tcp ports.
 *
 * <br>
 * <br>
 * Copyright (c) 2010 RWTH Aachen. All rights reserved.
 *
 * @author  Arne Haber
 * @date	29.10.2010
 */
public class TCPPortFactory implements IPortFactory {
    


    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createInPort()
     */
    @Override
    public <T> IInSimPort<T> createInPort() {
        return new TCPPort<T>();
    }

    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createOutPort()
     */
    @Override
    public <T> IOutSimPort<T> createOutPort() {
        return new TCPPort<T>();
    }

    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createForwardPort()
     */
    @Override
    public <T> IForwardPort<T> createForwardPort() {
        return new ForwardingTCPPort<T>();
    }
    
    
    
}
