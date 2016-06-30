/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package sim.sched;

import java.util.List;

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
class PortMap implements IPortMap {
    
    /**
     * Holds the information, if a port is tickfree (true) or not (false).
     * Each port is represented by the position in the array.
     */
    private final boolean[] tickfree;
    
    /**
     * Amount of tickfree ports
     */
    private int tickfreePortAmount;
    
    /**
     * Constructs a new port map and assigns port numbers to the given ports.
     * 
     * @param managed ports
     */
    public PortMap(List<IInSimPort<?>> ports) {
        int size = ports.size();
        tickfree = new boolean[size];
        for (int i = 0; i < size; i++) {
            tickfree[i] = true;
            ports.get(i).setPortNumber(i);
        }
        this.tickfreePortAmount = size;
    }
    
    /**
     * @see sim.sched.IPortMap#setPortBlocked(sim.port.IInSimPort)
     */
    @Override
    public void setPortBlocked(IInSimPort<?> port) {
        int nr = port.getPortNumber();
        if (tickfree[nr]) {
            tickfree[nr] = false;
            tickfreePortAmount--;
        }
    }
    
    /**
     * @see sim.sched.IPortMap#setPortTickfree(sim.port.IInSimPort)
     */
    @Override
    public void setPortTickfree(IInSimPort<?> port) {
        int nr = port.getPortNumber();
        if (!tickfree[nr]) {
            tickfree[nr] = true;
            tickfreePortAmount++;
        }
    }
    
    /**
     * @see sim.sched.IPortMap#allPortsBlocked()
     */
    @Override
    public boolean allPortsBlocked() {
        return tickfreePortAmount == 0;
    }
    
    /**
     * @see sim.sched.IPortMap#isTickfree(sim.port.IInSimPort)
     */
    @Override
    public boolean isTickfree(IInSimPort<?> port) {
        return tickfree[port.getPortNumber()];
    }
}
