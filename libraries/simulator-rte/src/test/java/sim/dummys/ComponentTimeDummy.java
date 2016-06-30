package sim.dummys;

import sim.IScheduler;
import sim.error.ISimulationErrorHandler;
import sim.generic.AComponent;
import sim.generic.ISimComponent;
import sim.generic.ITimedComponent;
import sim.generic.Message;
import sim.port.IInPort;

/**
 * Is used to directly control the test components time.
 * 
 * @author Arne Haber
 * @version 05.02.2009
 */
public class ComponentTimeDummy extends AComponent implements ISimComponent, ITimedComponent {
    
    /**
     * Simulation error handler.
     */
    protected ISimulationErrorHandler handler;
    
    /**
     * Local time.
     */
    protected int time;
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IComponent#checkConstraints()
     */
    @Override
    public void checkConstraints() {
        
    }
    
    @Override
    public ISimulationErrorHandler getErrorHandler() {
        return this.handler;
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IComponent#getLocalTime()
     */
    @Override
    public int getLocalTime() {
        
        return time;
    }
    
    /* (non-Javadoc)
     * @see sim.generic.IComponent#getName()
     */
    @Override
    public String getComponentName() {
        return null;
    }
    
    @Override
    public void handleMessage(IInPort<?> port, Message<?> message) {
        
    }
    
    @Override
    public void handleTick() {
        
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IComponent#setName(java.lang.String)
     */
    @Override
    public void setComponentName(String name) {
        
    }
    
    /**
     * @param time the time to set
     */
    public void setTime(int time) {
        this.time = time;
    }

    /*
     * (non-Javadoc)
     * @see sim.generic.IComponent#setup(sim.IScheduler)
     */
    @Override
    public void setup(IScheduler scheduler, ISimulationErrorHandler errorHandler) {
        this.handler = errorHandler;
    }
}
