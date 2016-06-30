package sim;

import sim.generic.TickedMessage;
import sim.port.IInSimPort;
import sim.port.IPortFactory;

/**
 * Prescribes methods for the simulation scheduler. 
 * 
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $LastChangedBy: ahaber $
 * @version $LastChangedDate: 2014-10-14 10:42:14 +0200 (Di, 14 Okt 2014) $<br>
 *          $LastChangedRevision: 3036 $
 */
public interface IScheduler extends IPortFactory {
    
    /**
     * Registers the given port 'port' by this scheduler and schedules the given
     * message, if possible.
     * 
     * @param port port to register
     * @param msg message to schedule
     * @return true, if message msg has been schedules immediately, else false.
     */
    boolean registerPort(IInSimPort<?> port, TickedMessage<?> msg);
    
    /**
     * Used to setup the scheduler. This way the scheduler knows which port
     * belongs to which component.
     * 
     * @param port port to map
     */
    void setupPort(IInSimPort<?> port);
    
    /**
     * Initializes or resets the scheduler.
     */
    void init();
    
    /**
     * The given factory fact is used to produce ports.
     * 
     * @param fact factory to use
     */
    void setPortFactory(IPortFactory fact);
    
    /**
     * @return the port facotry of the scheduler
     * @since 2.5.0
     */
    IPortFactory getPortFactory();
}
