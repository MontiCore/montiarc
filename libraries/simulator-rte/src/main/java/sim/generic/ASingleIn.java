package sim.generic;

import sim.IScheduler;
import sim.error.ISimulationErrorHandler;
import sim.port.IInPort;
import sim.port.IPort;
import sim.port.ITestPort;
import sim.port.Port;
import sim.sched.SchedulerFactory;

/**
 * 
 * Is used for components with just one incoming port and two or more outgoing
 * ports.
 *
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Date: 2015-03-19 14:43:21 +0100 (Do, 19 Mrz 2015) $<br>
 *          $Revision: 3136 $
 * @param <Tin>
 */
public abstract class ASingleIn<Tin> extends Port<Tin> implements ITestPort<Tin>, SimpleInPortInterface<Tin>, ISimComponent {
    
    /** Id assigned by the scheduler. */
    private int id = -1;

    /**
     * @see sim.port.ITestPort#getStream()
     */
    @Override
    public IStream<Tin> getStream() {
        if (testPort != null && testPort instanceof ITestPort) {
            return ((ITestPort<Tin>) testPort).getStream();            
        }
        else {
            return null;
        }
    }
    /**
     * Used to store a test port, if we are in test mode.
     */
    private IPort<Tin> testPort;
    
    /** Handles ArcSimProblemReports. */
    private ISimulationErrorHandler errorHandler;
  
    /** Name of this component. */
    private String componentName;

    /**
     * Default constructor.
     */
    public ASingleIn() {
        super();
    }

    /**
     * @return the errorHandler
     */
    @Override
    public ISimulationErrorHandler getErrorHandler() {
        return errorHandler;
    }
    
    /* (non-Javadoc)
     * @see sim.generic.ISimComponent#getName()
     */
    @Override
    public String getComponentName() {
        return this.componentName;
    }

    /**
     * @return the portIn
     */
    public IInPort<Tin> getPortIn() {
        return this;
    }
    

    /**
     * @see sim.port.Port#accept(sim.generic.TickedMessage)
     */
    @Override
    public void accept(TickedMessage<? extends Tin> message) {
        super.accept(message);
        if (testPort != null) {
            testPort.accept(message);
        }
    }

    /*
     * (non-Javadoc)
     * @see sim.generic.ISimComponent#handleMessage(sim.generic.IIncomingPort, sim.generic.Message)
     */
    @Override
    @SuppressWarnings("unchecked") 
    public void handleMessage(IInPort<?> port, Message<?> message) {
        messageReceived((Tin) message.getData());
    }

    
    /**
     * @param errorHandler the errorHandler to set
     */
    protected void setErrorHandler(ISimulationErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.ISimComponent#setName(java.lang.String)
     */
    @Override
    public void setComponentName(String name) {
        this.componentName = name;
    }
    
    
    /*
     * (non-Javadoc)
     * @see sim.generic.ISimComponent#setup(sim.IScheduler)
     */
    @Override
    public void setup(IScheduler scheduler, sim.error.ISimulationErrorHandler errorHandler) {
        IScheduler localSched = SchedulerFactory.createSingleInScheduler();
        localSched.setupPort(this);
        localSched.setPortFactory(scheduler.getPortFactory());
        super.setup(this, localSched);
        setErrorHandler(errorHandler);
        
        IInPort<Tin> tp = scheduler.createInPort();
        if (!(tp.getClass().getName().equals(Port.class.getName()))) {
            this.testPort = (IPort<Tin>) tp;
        }
    }

    /**
     * @return the scheduler
     */
    protected IScheduler getScheduler() {
        return super.getScheduler();
    }

    /**
     * @param scheduler the scheduler to set
     */
    protected void setScheduler(IScheduler scheduler) {
        // ignored
    }
    
    /**
     * @see sim.generic.ISimComponent#setSimulationId(int)
     */
    @Override
    public final void setSimulationId(int id) {
        this.id = id;
    }

    /**
     * @see sim.generic.ISimComponent#getSimulationId()
     */
    @Override
    public final int getSimulationId() {
        return this.id;
    }
    
}
