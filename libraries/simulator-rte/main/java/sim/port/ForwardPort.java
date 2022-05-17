/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.sched.IScheduler;
import sim.comp.ISimComponent;
import sim.message.Message;
import sim.message.TickedMessage;
import sim.Automaton.Transitionpath;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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
        innerPorts = new LinkedHashSet<>();
    }
    
    /**
     * @see sim.generic.IInPort#acceptMessage(java.lang.Object)
     */
    @Override
    public void accept(T message) {
        accept(Message.of(message));
    }
    
    /*
     *
     * @see sim.generic.IInPort#receiveMessage(sim.message.TickedMessage)
     */
    @Override
    public void accept(TickedMessage<? extends T> message) {
        for (IInPort<? super T> p : this.getInnerPorts()) {
            p.accept(message);
        }
    }

    @Override
    public void symbolicAccept(Message<Transitionpath> message) {
        for (IInPort<? super T> p : this.getInnerPorts()) {
            p.symbolicAccept(message);
        }
    }

    /*
     *
     * @seesim.generic.IArchitectureComponentPort#addReceiver(sim.generic.
     * IInPort)
     */
    @Override
    public void add(IInSimPort<? super T> port) {
        if (!this.getInnerPorts().contains(port)) {
            this.getInnerPorts().add(port);
        }
    }
    
    
    /**
     * @see sim.generic.IOutPort#addReceiver(sim.generic.IInPort)
     */
    @Override
    public void addReceiver(IInPort<? super T> receiver) {
        if (receiver instanceof IInSimPort) {
            add((IInSimPort<? super T>) receiver);            
        }
        
    }
    
    /*
     *
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
    
    /**
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
     *
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
     *
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

    /**
     * @see IPort#isConnected()
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

    /**
     * @see IPort#processBufferedMsgs()
     */
    @Override
    public void processBufferedMsgs() {
        for (IInSimPort<? super T> p : getInnerPorts()) {
            p.processBufferedMsgs();
        }
    }

    /*
     *
     * @see
     * sim.generic.IArchitectureComponentPort#removeEncapsulatedPort(sim.generic
     * .IInPort)
     */
    @Override
    public boolean removeEncapsulatedPort(IInSimPort<? super T> port) {
        return this.getInnerPorts().remove(port);
    }

    /**
     * @see sim.generic.IOutPort#sendMessage(TickedMessage)
     */
    @Override
    public void send(TickedMessage<T> message) {
        accept(message);
        
    }

    @Override
    public void symbolicSend(Message<Transitionpath> message){
        symbolicAccept(message);
    }

    /**
     * @see sim.generic.IOutPort#setComponent(ISimComponent)
     */
    @Override
    public void setComponent(ISimComponent component) {
        this.component = component;
    }

    /**
     * @see IPort#setConnected()
     */
    @Override
    public void setConnected() {
        for (IInSimPort<? super T> p : getInnerPorts()) {
            p.setConnected();
        }
    }

    /*
     *
     * @see sim.generic.IInPort#setup(sim.comp.ISimComponent,
     * sim.sched.IScheduler)
     */
    @Override
    public void setup(ISimComponent component, IScheduler scheduler) {
        setComponent(component);
        for (IInSimPort<? super T> p : this.getInnerPorts()) {
            p.setup(component, scheduler);
        }
    }

    /**
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