/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.generic.IStream;
import sim.generic.Stream;
import sim.generic.TickedMessage;

/**
 * Forward port that additionally stores the transmitted stream.
 *
 *          $Date: 2014-03-25 15:02:56 +0100 (Di, 25 Mrz 2014) $
 *
 */
public class TestForwardPort<T> extends ForwardPort<T> {
    
    /**
     * Stores received messages. 
     */
    private final IStream<T> stream;
    
    /**
     * Constructor for sim.port.TestForwardPort
     */
    public TestForwardPort() {
        stream = new Stream<T>();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void accept(TickedMessage<? extends T> message) {
        super.accept(message);
        stream.add((TickedMessage<T>) message);
        stream.pollLastMessage();
    }
    
}
