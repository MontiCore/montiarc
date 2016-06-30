/**
 * 
 */
package sim.port;

import sim.generic.IStream;
import sim.generic.Stream;
import sim.generic.TickedMessage;

/**
 * Can be used in Tests as an outgoing port. Received messages are stored 
 * in the field stream.
 *
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $LastChangedBy: ahaber $
 * @version $LastChangedDate: 2014-03-26 18:05:00 +0100 (Mi, 26 Mrz 2014) $<br>
 *          $LastChangedRevision: 2805 $
 * @param <T> type of the port
 */
public class TestPort<T> extends Port<T> implements ITestPort<T> {
    
    /**
     * Stores received messages. 
     */
    private final IStream<T> stream;
    
    /**
     * 
     */
    public TestPort() {
        super();
        stream = new Stream<T>();
    }


    /* (non-Javadoc)
     * @see sim.generic.IIncomingPort#acceptMessage(sim.generic.TickedMessage)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void accept(TickedMessage<? extends T> message) {
        super.accept(message);
        stream.add((TickedMessage<T>) message);
        stream.pollLastMessage();
    }

    /**
     * @see sim.port.ITestPort#getStream()
     */
    @SuppressWarnings("unchecked")
    @Override
    public IStream<T> getStream() {
        IStream<T> s = stream;
        if (!getReceivers().isEmpty()) {
            IInPort<? super T> rec = getReceivers().iterator().next();
            if (rec instanceof ITestPort) {
                s = ((ITestPort<T>) rec).getStream();
            }
        }
        return s;
    }
    
}
