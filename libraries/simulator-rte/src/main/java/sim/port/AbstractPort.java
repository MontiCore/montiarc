/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.IScheduler;
import sim.generic.ISimComponent;

/**
 * Abstract port implementation.
 * 
 * @author (last commit) $Author: ahaber $
 * @version $Revision: 2874 $, $Date: 2014-06-24 14:38:16 +0200 (Di, 24 Jun 2014) $
 * @since 2.5.0
 */
public abstract class AbstractPort<T> implements IPort<T> {
    
    /** The component owning this port. */
    private ISimComponent component;
    
    /** Flags, if this port is connected. */
    private boolean isConnected;
    
    /** Port number. */
    private int number = -1;
    
    /** The scheduler from this port. */
    private IScheduler scheduler;
    
    @Override
    public ISimComponent getComponent() {
        return component;
    }
    
    /**
     * @see sim.port.IInSimPort#getPortNumber()
     */
    @Override
    public int getPortNumber() {
        return number;
    }
    
    protected IScheduler getScheduler() {
        return this.scheduler;
    }
    
    @Override
    public boolean isConnected() {
        return isConnected;
    }
    
    /**
     * @param component the component to set
     */
    public void setComponent(ISimComponent component) {
        this.component = component;
    }
    
    @Override
    public void setConnected() {
        isConnected = true;
        
    }
    
    /**
     * @see sim.port.IInSimPort#setPortNumber(int)
     */
    @Override
    public void setPortNumber(int nr) {
        this.number = nr;
    }
    
    protected void setScheduler(IScheduler scheduler) {
        this.scheduler = scheduler;
    }
    
}
