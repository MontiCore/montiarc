/**
 * 
 */
package sim.generic;

import sim.port.IInPort;

/**
 * Simulation specific methods of a component.
 *
 * <br>
 * <br>
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author:$
 * @version $Date:$<br>
 *          $Revision:$
 * @since   2.2.0 (09.04.2013)
 */
public interface ISimComponent extends IComponent {
    /**
     * Is called by the scheduler.
     * 
     * @param port the port that received the message
     * @param message the message that is received by the port
     */
    void handleMessage(IInPort<?> port, Message<?> message);
    
    /**
     * Sets the name from this component with the given name.
     * 
     * @param componentName name to set
     */
    void setComponentName(String componentName);
    
    /**
     * Stimulates the component to emit a tick on all outgoing ports and to increment the local time.
     */
    void handleTick();
    
    /**
     * Used by the scheduler to set the simulation id.
     * @param id simulation id to set.
     */
    void setSimulationId(int id);
    
    /**
     * 
     * @return the simulation id from this component.
     */
    int getSimulationId();
    
}
