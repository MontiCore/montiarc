/**
 * 
 */
package sim.port;

import java.util.Collection;

import sim.generic.ISimComponent;

/**
 * Simulation specific methods of an outgoing port.
 *  *
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
public interface IOutSimPort<T> extends IOutPort<T> {
    /**
     * Adds the given receiver to the receivers of this port.
     * 
     * @param receiver receiver to add
     */
    void addReceiver(IInPort<? super T> receiver);
    
    /**
     * @return the component that owns this port
     */
    ISimComponent getComponent();
    
    /**
     * @return a set of receivers of this port
     */
    Collection<IInPort<? super T>> getReceivers();
    
    /**
     * Sets the component of this port.
     * 
     * @param component the component that owns this port
     */
    void setComponent(ISimComponent component);
}
