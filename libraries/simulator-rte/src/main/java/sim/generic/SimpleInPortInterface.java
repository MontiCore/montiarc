package sim.generic;

/**
 * 
 * Is used as the port interface for 
 * {@link ASingleIn}.
 *
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Date: 2012-11-16 16:09:34 +0100 (Fr, 16 Nov 2012) $<br>
 *          $Revision: 1704 $
 * @param <Tin> data type of the single in port
 */
public interface SimpleInPortInterface<Tin> {
    
    /**
     * Is called, if a message is received on the incoming port.
     * 
     * @param message the received message
     */
    void messageReceived(Tin message);
    
}
