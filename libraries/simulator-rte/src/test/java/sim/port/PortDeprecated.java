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
 * Old default port implementation. Use {@link Port} instead. PortDeprecated is
 * for tests only. <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 * 
 * @author (last commit) $LastChangedBy: ahaber $
 * @version $LastChangedDate: 2015-02-05 17:20:14 +0100 (Do, 05 Feb 2015) $<br>
 *          $LastChangedRevision: 3114 $
 * @param <T> data type of this port
 */
public class PortDeprecated<T> extends AbstractPort<T> implements IPort<T> {
    
    /**
     * Flags, if a tick has been received but not processed yet.
     */
    protected boolean blockingTickReceived;
    
    /**
     * Used to buffer messages.
     */
    protected Queue<TickedMessage<?>> bufferQueue;
    
    /**
     * A set of additional receivers. It is used if one outgoing port contains
     * more then one receiver.
     */
    protected List<IInPort<? super T>> receivers;
    
    /**
     * Default constructor.
     */
    protected PortDeprecated() {
        receivers = new ArrayList<IInPort<? super T>>();
        bufferQueue = new LinkedList<TickedMessage<?>>();
        blockingTickReceived = false;
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
        if (!blockingTickReceived && getScheduler() != null) {
            boolean success = getScheduler().registerPort(this, message);
            if (!success && message.isTick()) {
                blockingTickReceived = true;
            }
            else if (!success) {
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
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#wakeUp()
     */
    @Override
    public void processBufferedMsgs() {
        TickedMessage<?> nextMsg = null;
        
        while ((nextMsg = bufferQueue.peek()) != null) {
            boolean success = getScheduler().registerPort(this, nextMsg);
            if (success) {
                bufferQueue.poll();
            }
            else if (nextMsg.isTick()) {
                blockingTickReceived = true;
                bufferQueue.poll();
                break;
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
     * @see sim.generic.IIncomingPort#setup(sim.generic.ISimComponent,
     * sim.IScheduler)
     */
    @Override
    public void setup(ISimComponent component, IScheduler scheduler) {
        this.setComponent(component);
        setScheduler(scheduler);
        if (component != null) {
            scheduler.setupPort(this);
        }
    }
    
    @Override
    public String toString() {
        return hashCode() + ": " + blockingTickReceived + " -> " + bufferQueue.toString();
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#wakeUp()
     */
    @Override
    public void wakeUp() {
        if (hasTickReceived()) {
            blockingTickReceived = true;
            bufferQueue.poll();
        }
        else {
            blockingTickReceived = false;
            processBufferedMsgs();
        }
    }
}
