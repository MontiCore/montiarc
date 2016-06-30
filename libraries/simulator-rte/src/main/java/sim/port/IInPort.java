package sim.port;

import sim.generic.TickedMessage;

/**
 * An incoming port is used to receive messages of type T asynchronously. 
 * 
 * <br>
 * <br>
 * Copyright (c) 2010 RWTH Aachen. All rights reserved.
 * 
 * @author Arne Haber
 * @version 13.10.2008
 * @param <T> data type of this port
 */
public interface IInPort<T> {
    
    /**
     * Accepts the given data message of type T.
     * 
     * @param data message to accept
     */
    void accept(T data);
    
    /**
     * Accepts the given message on this port.
     * 
     * @param message message to accept
     */
    void accept(TickedMessage<? extends T> message);
}
