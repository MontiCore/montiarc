/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.port;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import sim.IScheduler;
import sim.generic.ISimComponent;
import sim.generic.Message;
import sim.generic.TickedMessage;

/**
 * A port that is to be used with a master/slave scheduler.
 *
 *
 * @param <T> data type of this port
 */
public class MasterSlavePort<T> implements IPort<T> {
    
    /**
     * Port number.
     */
    private int number = -1;
    
    /**
     * To buffer messages.
     */
    private final Queue<TickedMessage<?>> bufferQueue;
    
    /** The component owning this port. */
    private ISimComponent component;
    
    /** Flags, if this port is connected. */
    private boolean isConnected;
    
    /**
     * A set of additional receivers. It is used if one outgoing port contains
     * more then one receiver.
     */
    private final List<IInPort<? super T>> receivers;
    
    /** The scheduler of this port. */
    private IScheduler scheduler;
    
    /**
     * Default constructor.
     */
    protected MasterSlavePort() {
        receivers = new ArrayList<IInPort<? super T>>();
        bufferQueue = new LinkedList<TickedMessage<?>>();
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#acceptMessage(java.lang.Object)
     */
    @Override
    public void accept(T data) {
        accept(Message.of(data));
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#acceptMessage(sim.generic.TickedMessage)
     */
    @Override
    public void accept(TickedMessage<? extends T> message) {
        if (!hasTickReceived() && scheduler != null) {
            boolean success = scheduler.registerPort(this, message);
            if (!success) {
                bufferQueue.offer(message);
            }
        }
        else {
            bufferQueue.offer(message);
        }
        
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IOutgoingPort#addReceiver(sim.generic.IIncomingPort)
     */
    @Override
    public void addReceiver(IInPort<? super T> receiver) {
        this.receivers.add(receiver);
        
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#getComponent()
     */
    @Override
    public ISimComponent getComponent() {
        return component;
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IOutPort#getReceivers()
     */
    @Override
    public Collection<IInPort<? super T>> getReceivers() {
        return receivers;
    }
    
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#hasTickReceived()
     */
    @Override
    public boolean hasTickReceived() {
        TickedMessage<?> msg = bufferQueue.peek();
        return (msg != null && msg.isTick());
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#hasUnprocessedMessages()
     */
    @Override
    public boolean hasUnprocessedMessages() {
        return bufferQueue.size() > 0;
    }
    
    /* (non-Javadoc)
     * @see sim.port.IInPort#isConnected()
     */
    @Override
    public boolean isConnected() {
        return isConnected;
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#processBufferedMsgs()
     */
    @Override
    public void processBufferedMsgs() {
        TickedMessage<?> nextMsg = null;
        
        while ((nextMsg = bufferQueue.peek()) != null) {
            boolean success = scheduler.registerPort(this, nextMsg);
            if (success) {
                bufferQueue.poll();
            }
            else {
                break;
            }
        }
        
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IOutgoingPort#sendMessage(sim.generic.TickedMessage)
     */
    @Override
    public void send(TickedMessage<T> message) {
        // send the given message to all additional receivers
        for (IInPort<? super T> receiver : receivers) {
            receiver.accept(message);
        }
        // and accept the message (role incoming port)
        accept(message);
        
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IOutgoingPort#setComponent(sim.generic.ISimComponent)
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
        isConnected = true;
        
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#setup(sim.generic.ISimComponent,
     * sim.IScheduler)
     */
    @Override
    public void setup(ISimComponent component, IScheduler scheduler) {
        this.component = component;
        this.scheduler = scheduler;
        if (component != null) {
            scheduler.setupPort(this);
        }
    }

    @Override
    public String toString() {
        return hashCode() + ": " + hasTickReceived() + " -> " + bufferQueue.toString();
    }

    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#wakeUp()
     */
    @Override
    public void wakeUp() {
        if (hasTickReceived()) {
            // remove blocking tick
            bufferQueue.poll();
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
