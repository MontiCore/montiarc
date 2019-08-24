/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.sched;

import sim.IScheduler;
import sim.error.NotImplementedYetException;
import sim.generic.ISimComponent;
import sim.generic.Message;
import sim.generic.TickedMessage;
import sim.port.DefaultPortFactory;
import sim.port.IForwardPort;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;
import sim.port.IPortFactory;

/**
 * Scheduler optimized for components with one incoming port.
 * 
 *
 *
 */
class SingleInScheduler implements IScheduler {
    
    /**
     * The port factory to use.
     */
    private IPortFactory portFactory;
    
    /**
     * Flags, if this scheduler is active.
     */
    private boolean isActive;
    
    /**
     * 
     */
    protected SingleInScheduler() {
        init();
        portFactory = new DefaultPortFactory();
        isActive = false;
        
    }
    
    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createInPort()
     */
    @Override
    public <T> IInSimPort<T> createInPort() {
        throw new NotImplementedYetException("A Single in Scheduler is attended to be used for a SingleInComponent. " +
                "As this component is its port itself, this method should never be called.");
    }
    
    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createOutPort()
     */
    @Override
    public <T> IOutSimPort<T> createOutPort() {
        return portFactory.createOutPort();
    }
    
    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createForwardPort()
     */
    @Override
    public <T> IForwardPort<T> createForwardPort() {
        throw new NotImplementedYetException("A Single in Scheduler is attended to be used for a SingleInComponent. " +
        "As this component is its port itself, this method should never be called.");
    }
    
    /* (non-Javadoc)
     * @see sim.IScheduler#registerPort(sim.generic.IIncomingPort, sim.generic.TickedMessage)
     */
    @Override
    public boolean registerPort(IInSimPort<?> port, TickedMessage<?> msg) {
        boolean success = false;
        ISimComponent comp = port.getComponent();
        
        if (!isActive) {
            isActive = true;
            if (msg.isTick()) {
                comp.handleTick();
                port.wakeUp();
            }
            else {
                comp.handleMessage(port, (Message<?>) msg);
                comp.checkConstraints();
            }
            success = true;
            isActive = false;
        }
        
        return success;
    }
    
    /* (non-Javadoc)
     * 
     * @see sim.IScheduler#mapPortToComponent(sim.generic.IInSimPort) */
    @Override
    public void setupPort(IInSimPort<?> port) {
    }
    
    /* (non-Javadoc)
     * @see sim.IScheduler#init()
     */
    @Override
    public void init() {
        
    }
    
    /* (non-Javadoc)
     * @see sim.IScheduler#setPortFactory(sim.port.IPortFactory)
     */
    @Override
    public void setPortFactory(IPortFactory fact) {
        this.portFactory = fact;
    }

    /**
     * @see sim.IScheduler#getPortFactory()
     */
    @Override
    public IPortFactory getPortFactory() {
        return this.portFactory;
    }
    
}
