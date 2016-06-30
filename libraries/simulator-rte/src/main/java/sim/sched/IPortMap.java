/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package sim.sched;

import sim.port.IInSimPort;

/**
 * Handles the information, which ports of a component are tickfree and which are not.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Revision: 2949 $,
 *          $Date: 2014-08-15 11:18:12 +0200 (Fr, 15 Aug 2014) $
 * @since   2.5.0
 *
 */
interface IPortMap {
    
    /**
     * Sets the given port to blocked. Thus it is not tickfree afterwards.
     * 
     * @param port port to set blocked
     */
    void setPortBlocked(IInSimPort<?> port);
    
    /**
     * Sets the given port to tickfree.
     * 
     * @param port tickfree port
     */
    void setPortTickfree(IInSimPort<?> port);
    
    /**
     * @return true, if no ports are tickfree, else false.
     */
    boolean allPortsBlocked();
    
    /**
     * Checks, if the given port is tickfree.
     * 
     * @param port port to check
     * @return true, if port is tickfree, else false.
     */
    boolean isTickfree(IInSimPort<?> port);
    
}
