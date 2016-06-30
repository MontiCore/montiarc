package sim.generic;

import java.io.Serializable;

/**
 * Abstract carrier data type that is used for messages in the simulation
 * framework. 
 * 
 * <br>
 * <br>
 * Copyright (c) 2010 RWTH Aachen. All rights reserved.
 * 
 * @author Arne Haber
 * @version 19.11.2008
 * @param <T> data type from this message
 */
public abstract class TickedMessage<T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6109585129447746214L;

    /**
     * Checks, if this message is either a Tick or a concrete Message.
     * 
     * @return true, if this message is a Tick.
     */
    public abstract boolean isTick();
}
