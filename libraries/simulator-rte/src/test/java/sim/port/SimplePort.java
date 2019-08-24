/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.port;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sim.IScheduler;
import sim.generic.ISimComponent;
import sim.generic.IStream;
import sim.generic.Message;
import sim.generic.Stream;
import sim.generic.TickedMessage;

/**
 * A Port that acts as an incoming and outgoing port. 
 * It contains a stream of type {@link IStream} to save 
 * and buffer messages that are transmitted by this port.
 *
 *
 * @param <T> data type of this port.
 * 
 */
public class SimplePort<T> extends AbstractPort<T> implements IPort<T> {
    
    /** A set of additional receivers. It is used if one outgoing port contains more then one receiver. */
    protected List<IInPort<? super T>> receivers;
    
    /** The incoming stream of this port. */
    protected IStream<T> stream;
    
    /**
     * 
     */
    public SimplePort() {
        stream = new Stream<T>();
        receivers = new ArrayList<IInPort<? super T>>();
    }
    
    /* (non-Javadoc)
     * @see sim.generic.IIncomingPort#acceptDataMessage(java.lang.Object)
     */
    @Override
    public void accept(T message) {
        accept(Message.of(message));
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#receiveMessage(sim.generic.ITickedMessage)
     * @note because Tin is a superclass of ? extends Tin this cast is typesave
     */
    @SuppressWarnings("unchecked")
    @Override
    public void accept(TickedMessage<? extends T> message) {
        // add message to incoming stream
        getIncomingStream().add((TickedMessage<T>) message);
        // scheduler should control the activation from this port
        if (getScheduler() != null) {
            getScheduler().registerPort(this, message);            
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

    /**
     * 
     * @return the incoming stream.
     */
    protected IStream<T> getIncomingStream() {
        return stream;
    }
    
    /**
     * 
     * @return the outgoing stream.
     */
    protected IStream<T> getOutgoingStream() {

        return stream;
    }
    
    
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IOutgoingPort#getReceivers()
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
        TickedMessage<? extends T> lastMsg = getIncomingStream().peekLastMessage();
        return lastMsg != null && lastMsg.isTick();
    }
    

    


    
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#hasUnprocessedMessages()
     */
    @Override
    public boolean hasUnprocessedMessages() {
        return getIncomingStream().getBuffer().size() > 0;
    }
    
    /* (non-Javadoc)
     * @see sim.port.IInPort#processBufferedMsgs()
     */
    @Override
    public void processBufferedMsgs() {
        // not needed in a SimplePort
        
    }

    /*
     * (non-Javadoc)
     * @see sim.generic.IOutgoingPort#sendMessage(sim.generic.ITimedMessage)
     */
    @Override
    public void send(TickedMessage<T> message) {
        // send the given message to all additional receivers
        for (IInPort<? super T> receiver : getReceivers()) {
            receiver.accept(message);
        }
        // and accept the message (role incoming port)
        accept(message);
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IIncomingPort#setup(sim.generic.ISimComponent, sim.IScheduler)
     */
    @Override
    public void setup(ISimComponent component, IScheduler scheduler) {
        setComponent(component);
        setScheduler(scheduler);
        if (component != null) {
            scheduler.setupPort(this);
        }
    }

    /* (non-Javadoc)
     * @see sim.generic.IIncomingPort#wakeUp()
     */
    @Override
    public void wakeUp() {
        // not needed in a SimplePort
        
    }

}
