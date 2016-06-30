/**
 * 
 */
package sim.port;

import sim.IScheduler;
import sim.generic.ISimComponent;

/**
 * Simulation specific methods of an incoming port.
 *
 * <br>
 * <br>
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author:$
 * @version $Date:$<br>
 *          $Revision:$
 * @since   2.2.0 (04.04.2013)
 * 
 * @param <T> data type of this port
 */
public interface IInSimPort<T> extends IInPort<T> {
    /**
     * @return the component that owns this port
     */
    ISimComponent getComponent();
    
    /**
     * @return true, if last buffered message is a tick.
     */
    boolean hasTickReceived();
    
    /**
     * Checks, if this port contains unsent messages.
     * 
     * @return true, if this ports has messages in its buffer.
     */
    boolean hasUnprocessedMessages();
    
    /**
     * 
     * @return true, if this port is connected, else false.
     */
    boolean isConnected();

    /**
     * 
     */
    void processBufferedMsgs();

    /**
     * Sets the port to a connected state.
     */
    void setConnected();
    
    /**
     * Sets component and scheduler of this incoming port and
     * signs up this port on the scheduler.
     * 
     * @param component the component that owns this port
     * @param scheduler the scheduler that handles this port
     */
    void setup(ISimComponent component, IScheduler scheduler);
    
    /**
     * 
     */
    void wakeUp();
    
    /**
     * Used by the scheduler to set the port number from this port to the given number nr.
     * 
     * @param nr port number to set
     * @since 2.5.0
     */
    void setPortNumber(int nr);
    
    /**
     * @return the port number from this port
     * @since 2.5.0
     */
    int getPortNumber();
}
