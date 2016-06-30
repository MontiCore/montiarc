package sim.generic;

/**
 * Interface for message factories that create Ticks and TickedMessages that are
 * used in the simulation. 
 * 
 * <br>
 * <br>
 * Copyright (c) 2010 RWTH Aachen. All rights reserved.
 * 
 * @author Arne Haber
 * @version 07.03.2009
 */
public interface IMessageFactory {
    
    /**
     * Creates a Message with the given data.
     * 
     * @param <T> type from the data
     * @param data message content
     * @return a TickedMessage that transports the given data
     */
    <T> TickedMessage<T> getMessage(T data);
    
    /**
     * @param <T> type from the tick
     * @param tickType type name
     * @return tick from the given type
     */
    <T> Tick<T> getTick(String tickType);
    
}
