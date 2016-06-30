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
 * Default port implementation.
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 * 
 * @author (last commit) $LastChangedBy: ahaber $
 * @version $LastChangedDate: 2014-11-17 17:17:34 +0100 (Mo, 17 Nov 2014) $<br>
 *          $LastChangedRevision: 3051 $
 * @param <T> data type of this port
 */
public class Port<T> implements IPort<T> {
    
    /**
     * Port number.
     */
    private int number = -1;
    
    /**
     * Used to buffer messages.
     */
    private final Queue<TickedMessage<?>> bufferQueue;
    
    /**
     * A set of additional receivers. It is used if one outgoing port contains
     * more then one receiver.
     */
    private final List<IInPort<? super T>> receivers;

    /** The component owning this port. */
    private ISimComponent component;
    
    /** Flags, if this port is connected. */
    private boolean isConnected;
    
    /**
     * Flags, if this port is currently scheduled.
     */
    private boolean isInSchedule;
    
    /** The scheduler of this port. */
    private IScheduler scheduler;
    
    /**
     * Default constructor.
     */
    protected Port() {
        receivers = new ArrayList<IInPort<? super T>>();
        bufferQueue = new LinkedList<TickedMessage<?>>();
        isInSchedule = false;
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#acceptMessage(java.lang.Object)
     */
    @Override
    public final void accept(T data) {
        accept(Message.of(data));
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#acceptMessage(sim.generic.TickedMessage)
     */
    @Override
    public void accept(TickedMessage<? extends T> message) {
        if (!isInSchedule && !hasTickReceived() && scheduler != null) {
            isInSchedule = true;
            boolean success = scheduler.registerPort(this, message);
            if (!success) {
                bufferQueue.offer(message);
            }
            isInSchedule = false;
        }
        else {
            bufferQueue.offer(message);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IOutgoingPort#addReceiver(sim.generic.IIncomingPort)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void addReceiver(IInPort<? super T> receiver) {
        this.receivers.add(receiver);
        if (!bufferQueue.isEmpty()) {
            List<TickedMessage<?>> copy = new ArrayList<TickedMessage<?>>(bufferQueue.size());
            copy.addAll(bufferQueue);
            for (TickedMessage<?> msg : copy) {
                receiver.accept((TickedMessage<T>) msg);                
            }
        }
        
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#getComponent()
     */
    @Override
    public final ISimComponent getComponent() {
        return component;
    }
    
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IOutPort#getReceivers()
     */
    @Override
    public final Collection<IInPort<? super T>> getReceivers() {
        return receivers;
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IInSimPort#hasTickReceived()
     */
    @Override
    public final boolean hasTickReceived() {
        TickedMessage<?> msg = bufferQueue.peek();
        return (msg != null && msg.isTick());
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IInSimPort#hasUnprocessedMessages()
     */
    @Override
    public final boolean hasUnprocessedMessages() {
        return bufferQueue.size() > 0;
    }
    
    /* (non-Javadoc)
     * @see sim.port.IInSimPort#isConnected()
     */
    @Override
    public boolean isConnected() {
        return isConnected;
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#wakeUp()
     */
    @Override
    public final void processBufferedMsgs() {
        if (!hasTickReceived()) {
            TickedMessage<?> nextMsg = null;
            
            while ((nextMsg = bufferQueue.peek()) != null) {
                isInSchedule = true;
                boolean success = scheduler.registerPort(this, nextMsg);
                if (success) {
                    bufferQueue.poll();
                }
                else {
                    break;
                }
                isInSchedule = false;
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
        setComponent(component);
        this.scheduler = scheduler;
        if (getComponent() != null) {
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
    public final void wakeUp() {
        // remove blocking tick, if there is one
        if (hasTickReceived()) {
            bufferQueue.poll();
        }
    }
    
    protected IScheduler getScheduler() {
        return this.scheduler;
    }

    /**
     * @see sim.port.IInSimPort#setPortNumber(int)
     */
    @Override
    public final void setPortNumber(int nr) {
        this.number = nr;
    }

    /**
     * @see sim.port.IInSimPort#getPortNumber()
     */
    @Override
    public final int getPortNumber() {
        return number;
    }
}
