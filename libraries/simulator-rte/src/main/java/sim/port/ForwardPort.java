/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import java.util.HashSet;
import java.util.Set;

import sim.IScheduler;
import sim.generic.ISimComponent;
import sim.generic.Message;
import sim.generic.TickedMessage;

/**
 * 
 * Is used to encapsulate the forwarded ports from inner components in one
 * architecture component port.
 *
 *
 * @param <T> data type of this port
 */
public class ForwardPort<T> implements IForwardPort<T> {
    
    private int number = -1;
    
    /** The component owning the encapsulating port. */
    private ISimComponent component;
    
    /** Contains the inner forwarded ports. */
    private final Set<IInSimPort<? super T>> innerPorts;
    
    /**
   * 
   */
    public ForwardPort() {
        innerPorts = new HashSet<IInSimPort<? super T>>();
    }
    
    /* (non-Javadoc)
     * @see sim.generic.IInPort#acceptMessage(java.lang.Object)
     */
    @Override
    public void accept(T message) {
        accept(Message.of(message));
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IInPort#receiveMessage(sim.generic.TickedMessage)
     */
    @Override
    public void accept(TickedMessage<? extends T> message) {
        for (IInPort<? super T> p : this.getInnerPorts()) {
            p.accept(message);
        }
    }
    
    /*
     * (non-Javadoc)
     * @seesim.generic.IArchitectureComponentPort#addReceiver(sim.generic.
     * IInPort)
     */
    @Override
    public void add(IInSimPort<? super T> port) {
        if (!this.getInnerPorts().contains(port)) {
            this.getInnerPorts().add(port);
        }
    }
    
    
    /* (non-Javadoc)
     * @see sim.generic.IOutPort#addReceiver(sim.generic.IInPort)
     */
    @Override
    public void addReceiver(IInPort<? super T> receiver) {
        if (receiver instanceof IInSimPort) {
            add((IInSimPort<? super T>) receiver);            
        }
        
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IInPort#getComponent()
     */
    @Override
    public ISimComponent getComponent() {
        return this.component;
    }
    
    /**
     * @return the first encapsulated port. This port is representative, because
     *         all other encapsulated ports contain the same stream. Returns null,
     *         if no port is registered.
     */
    private IInSimPort<? super T> getFirstPort() {
        IInSimPort<? super T> firstPort;
        if (!getInnerPorts().isEmpty()) {
            firstPort = getInnerPorts().iterator().next();
        }
        else {
            firstPort = null;
        }
        return firstPort;
    }
    
    /**
     * @return a set containing the inner forwarded ports.
     */
    private Set<IInSimPort<? super T>> getInnerPorts() {
        return this.innerPorts;
    }
    
    /* (non-Javadoc)
     * @see sim.generic.IOutPort#getReceivers()
     */
    @Override
    public Set<IInPort<? super T>> getReceivers() {
        Set<IInPort<? super T>> copy = new HashSet<IInPort<? super T>>();
        for (IInSimPort<? super T> p : getInnerPorts()) {
            copy.add(p);
        }
        return copy;
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IInPort#hasTickReceived()
     */
    @Override
    public boolean hasTickReceived() {
        IInSimPort<? super T> fp = getFirstPort();
        if (fp != null) {
            return getFirstPort().hasTickReceived();
        }
        else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see sim.generic.IInPort#hasUnprocessedMessages()
     */
    @Override
    public boolean hasUnprocessedMessages() {
        IInSimPort<? super T> fp = getFirstPort();
        if (fp != null) {
            return getFirstPort().hasUnprocessedMessages();
        }
        else {
            return false;            
        }
    }

    /* (non-Javadoc)
     * @see sim.port.IInPort#isConnected()
     */
    @Override
    public boolean isConnected() {
        IInSimPort<? super T> fp = getFirstPort();
        if (fp != null) {
            return getFirstPort().isConnected();
        }
        else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see sim.port.IInPort#processBufferedMsgs()
     */
    @Override
    public void processBufferedMsgs() {
        for (IInSimPort<? super T> p : getInnerPorts()) {
            p.processBufferedMsgs();
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * sim.generic.IArchitectureComponentPort#removeEncapsulatedPort(sim.generic
     * .IInPort)
     */
    @Override
    public boolean removeEncapsulatedPort(IInSimPort<? super T> port) {
        return this.getInnerPorts().remove(port);
    }

    /* (non-Javadoc)
     * @see sim.generic.IOutPort#sendMessage(sim.generic.TickedMessage)
     */
    @Override
    public void send(TickedMessage<T> message) {
        accept(message);
        
    }

    /* (non-Javadoc)
     * @see sim.generic.IOutPort#setComponent(sim.generic.ISimComponent)
     */
    @Override
    public void setComponent(ISimComponent component) {
        this.component = component;
    }

    /* (non-Javadoc)
     * @see sim.port.IInPort#setConnected()
     */
    @Override
    public void setConnected() {
        for (IInSimPort<? super T> p : getInnerPorts()) {
            p.setConnected();
        }
    }

    /*
     * (non-Javadoc)
     * @see sim.generic.IInPort#setup(sim.generic.ISimComponent,
     * sim.IScheduler)
     */
    @Override
    public void setup(ISimComponent component, IScheduler scheduler) {
        setComponent(component);
        for (IInSimPort<? super T> p : this.getInnerPorts()) {
            p.setup(component, scheduler);
        }
    }

    /* (non-Javadoc)
     * @see sim.generic.IInPort#wakeUp()
     */
    @Override
    public void wakeUp() {
        for (IInSimPort<? super T> p : getInnerPorts()) {
            p.wakeUp();
        }
    }

    /**
     * @see sim.port.IInSimPort#setPortNumber(int)
     */
    @Override
    public void setPortNumber(int nr) {
        this.number = nr;
    }

    /**
     * @see sim.port.IInSimPort#getPortNumber()
     */
    @Override
    public int getPortNumber() {
        return number;
    }

}
