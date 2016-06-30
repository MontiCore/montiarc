/**
 * 
 */
package sim.port;

import java.util.HashSet;
import java.util.Set;

import sim.generic.ISimComponent;
import sim.generic.IStream;
import sim.generic.Stream;
import sim.generic.TickedMessage;

/**
 * Implementation of an outgoing port. 
 *
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $LastChangedBy: ahaber $
 * @version $LastChangedDate: 2013-06-05 17:33:36 +0200 (Mi, 05 Jun 2013) $<br>
 *          $LastChangedRevision: 2376 $
 * @param <T> data type of this port
 * 
 */
public class OutPort<T> implements IOutSimPort<T> {

    /**
     * Set of receiving incoming ports that are connected to this outgoing port.
     */
    protected Set<IInPort<? super T>> receivers;
    
    /**
     * Component that owns this port.
     */
    protected ISimComponent component;
    
    /**
     * Stream that is transmitted via this port.
     */
    protected IStream<T> stream;
    
    
    /**
     * 
     */
    public OutPort() {
        receivers = new HashSet<IInPort<? super T>>();
        
        stream = new Stream<T>();
    }
    
    /* (non-Javadoc)
     * @see sim.port.IOutPort#addReceiver(sim.port.IInPort)
     */
    @Override
    public void addReceiver(IInPort<? super T> receiver) {
        this.receivers.add(receiver);
        
        for (TickedMessage<T> msg : stream.getHistory()) {
            receiver.accept(msg);
        }
    }

    /* (non-Javadoc)
     * @see sim.port.IOutPort#getComponent()
     */
    @Override
    public ISimComponent getComponent() {

        return this.component;
    }

    /* (non-Javadoc)
     * @see sim.port.IOutPort#getReceivers()
     */
    @Override
    public Set<IInPort<? super T>> getReceivers() {
        return receivers;
    }

    /* (non-Javadoc)
     * @see sim.port.IOutPort#sendMessage(sim.generic.TickedMessage)
     */
    @Override
    public void send(TickedMessage<T> message) {
        for (IInPort<? super T> rec : receivers) {
            rec.accept(message);
        }
        stream.add(message);
        stream.pollLastMessage();
    }

    /* (non-Javadoc)
     * @see sim.port.IOutPort#setComponent(sim.generic.ISimComponent)
     */
    @Override
    public void setComponent(ISimComponent component) {
        this.component = component;
    }
    
}
