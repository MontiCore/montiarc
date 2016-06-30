package sim.port;

import sim.generic.TickedMessage;

/**
 * An outgoing port is used to send messages to connected 
 * receivers.
 * 
 * <br>
 * <br>
 * Copyright (c) 2010 RWTH Aachen. All rights reserved.
 * 
 * @author Arne Haber
 * @version 13.10.2008
 * 
 * @param <T> data type of this port
 */
public interface IOutPort<T> {
        
    /**
     * Sends the given {@link TickedMessage} to all receiving ports.
     * 
     * @param message message to send
     */
    void send(TickedMessage<T> message);
}
