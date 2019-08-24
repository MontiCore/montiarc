/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.port;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import sim.IScheduler;
import sim.generic.ISimComponent;
import sim.generic.Message;
import sim.generic.TickedMessage;

/**
 * Stores all accepted messages in a buffer. They may be accessed using the poll method.
 *
 *
 * @param <T> data type of the port         
 */
public class MessageStoringPort<T> implements IPort<T> {

    /**
     * Port number.
     */
    private int number = -1;
    
    /**
     * Queues all received messages.
     */
    private final Queue<T> buffer;
    
    /** Flags, if this port is connected. */
    protected boolean isConnected;
    
    /**
     * 
     */
    public MessageStoringPort() {
        buffer = new LinkedList<T>();
    }
    
    /* (non-Javadoc)
     * @see sim.port.IInPort#accept(java.lang.Object)
     */
    @Override
    public void accept(T data) {
        accept(Message.of(data));
        
    }

    /* (non-Javadoc)
     * @see sim.port.IInPort#accept(sim.generic.TickedMessage)
     */
    @Override
    public void accept(TickedMessage<? extends T> message) {
        if (message instanceof Message) {
            @SuppressWarnings("unchecked")
            Message<T> casted = (Message<T>) message;
            buffer.offer(casted.getData());
        }
    }

    /* (non-Javadoc)
     * @see sim.port.IOutPort#addReceiver(sim.port.IInPort)
     */
    @Override
    public void addReceiver(IInPort<? super T> receiver) {
        
        
    }

    /* (non-Javadoc)
     * @see sim.port.IInPort#getComponent()
     */
    @Override
    public ISimComponent getComponent() {
        
        return null;
    }
    
    /* (non-Javadoc)
     * @see sim.port.IOutPort#getReceivers()
     */
    @Override
    public Collection<IInPort<? super T>> getReceivers() {
        
        return null;
    }

    /* (non-Javadoc)
     * @see sim.port.IInPort#hasTickReceived()
     */
    @Override
    public boolean hasTickReceived() {
        
        return false;
    }

    /* (non-Javadoc)
     * @see sim.port.IInPort#hasUnprocessedMessages()
     */
    @Override
    public boolean hasUnprocessedMessages() {
        
        return false;
    }

    /* (non-Javadoc)
     * @see sim.port.IInPort#isConnected()
     */
    @Override
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Retrieves and removes the head of the message buffer and returns it. 
     * 
     * @return the head of the message buffer, or null if the buffer is empty
     */
    public T pollMessage() {
        return buffer.poll();
    }

    /* (non-Javadoc)
     * @see sim.port.IInPort#processBufferedMsgs()
     */
    @Override
    public void processBufferedMsgs() {
        
        
    }

    /* (non-Javadoc)
     * @see sim.port.IOutPort#send(sim.generic.TickedMessage)
     */
    @Override
    public void send(TickedMessage<T> message) {
        accept(message);
        
    }

    /* (non-Javadoc)
     * @see sim.port.IOutPort#setComponent(sim.generic.ISimComponent)
     */
    @Override
    public void setComponent(ISimComponent component) {
        
        
    }

    /* (non-Javadoc)
     * @see sim.port.IInPort#setConnected()
     */
    @Override
    public void setConnected() {
        isConnected = true;
        
    }
    
    /* (non-Javadoc)
     * @see sim.port.IInPort#setup(sim.generic.ISimComponent, sim.IScheduler)
     */
    @Override
    public void setup(ISimComponent component, IScheduler scheduler) {
        
        
    }

    /* (non-Javadoc)
     * @see sim.port.IInPort#wakeUp()
     */
    @Override
    public void wakeUp() {
        
        
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
