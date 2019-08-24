/* (c) https://github.com/MontiCore/monticore */
/* generated from model sim.comp.MessageAbstraction*/
/* generated by template mc.umlp.arc.component.Component*/
package sim.comp.gen;

/**
 * <br>
 * <br>
 * Java representation of component MessageAbstraction.<br>
 * <br>
 * Generated with MontiArc 2.5.0-SNAPSHOT.<br>
 * 
 */
public abstract class AMessageAbstraction<T> extends sim.generic.ATimedComponent implements sim.comp.gen.interfaces.IMessageAbstraction<T>,
                sim.generic.ISimComponent {
    
    /* generated by template mc.umlp.arc.component.port.PortAttributes */
    private sim.port.IInSimPort<T> timeAndDataIn;
    
    /* generated by template mc.umlp.arc.component.port.PortAttributes */
    private sim.port.IOutPort<java.lang.Object> timeOut;
    
    public AMessageAbstraction() {
        super();
        
    }
    
    /* generated by template mc.umlp.arc.component.methods.CheckConstraints */
    /* (non-Javadoc)
     * @see sim.generic.IComponent#checkConstraints() */
    @Override
    public void checkConstraints() {
        
        // Empty implementation. To add handcoded constraints override
        // this method!
    }/* generated by template mc.umlp.arc.component.methods.Constructor */
    
    /* generated by template mc.umlp.arc.component.port.PortAttributesGetter */
    public sim.port.IInPort<T> getTimeAndDataIn() {
        sim.port.IInPort<T> _port = null;
        
        _port = timeAndDataIn;
        return _port;
    }
    
    /* generated by template mc.umlp.arc.component.port.PortAttributesGetter */
    public sim.port.IOutPort<java.lang.Object> getTimeOut() {
        sim.port.IOutPort<java.lang.Object> _port = null;
        if (timeOut == null) {
            timeOut = getScheduler().createOutPort();
        }
        
        _port = timeOut;
        return _port;
    }
    
    /* generated by template mc.umlp.arc.component.methods.HandleMessage */
    /* (non-Javadoc)
     * @see sim.generic.IComponent#handleMessage(sim.port.IInPort,
     * sim.generic.Message<?>) */
    @Override
    public void handleMessage(sim.port.IInPort<?> port, sim.generic.Message<?> message) {
        if (port == getTimeAndDataIn()) {
            treatTimeAndDataIn((T) message.getData());
        }
        
    }
    
    /* generated by template mc.umlp.arc.component.methods.HandleTick */
    /* (non-Javadoc)
     * @see sim.generic.AComponent#handleTick() */
    @Override
    public void handleTick() {
        
        /* generated by template mc.umlp.arc.component.methods.HandleTickPorts */
        getTimeOut().send(sim.generic.Tick.<java.lang.Object> get());
        
        incLocalTime();
        timeStep();
        
    }
    
    /* generated by template mc.umlp.arc.component.port.PortOutDelegate */
    /**
     * Is used to send messages through the outgoing port timeOut.
     */
    protected void sendTimeOut(final java.lang.Object message) {
        this.getTimeOut().send(sim.generic.Message.of(message));
        
    }
    
    /* generated by template mc.umlp.arc.component.port.PortAttributesSetter */
    public void setTimeOut(sim.port.IPort<java.lang.Object> port) {
        if (this.timeOut == null) {
            this.timeOut = port;
        }
        else {
            ((sim.port.IOutSimPort<java.lang.Object>) this.timeOut).addReceiver(port);
        }
        
    }
    
    /* generated by template mc.umlp.arc.component.methods.Setup */
    /* (non-Javadoc)
     * @see sim.generic.IComponent#setup(sim.IScheduler,
     * sim.error.ISimulationErrorHandler) */
    @Override
    public void setup(sim.IScheduler scheduler, sim.error.ISimulationErrorHandler errorHandler) {
        
        // set scheduler
        setScheduler(scheduler);
        // set the errorHandler
        setErrorHandler(errorHandler);
        setComponentName("sim.comp.MessageAbstraction");
        
        /* generated by template mc.umlp.arc.component.methods.setup.AtomicPorts */
        this.timeAndDataIn = scheduler.createInPort();
        this.timeAndDataIn.setup(this, scheduler);
    }
    
    /* generated by template mc.umlp.arc.component.port.PortInDelegate */
    /**
     * Is called from the simulation framework, if a message is received on port
     * timeAndDataIn.
     */
    protected abstract void treatTimeAndDataIn(final T message);
    
}
