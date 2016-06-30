/**
 * 
 */
package sim.port;


/**
 * Produces ports.
 *
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $LastChangedBy: ahaber $
 * @version $LastChangedDate: 2013-04-05 10:19:04 +0200 (Fr, 05 Apr 2013) $<br>
 *          $LastChangedRevision: 2147 $
 */
public interface IPortFactory {
    
    /**
     * Creates an incoming port.
     * 
     * @param <T> type of the port
     * @return an incomin port
     */
    <T> IInSimPort<T> createInPort();
    
    /**
     * Creates an outgoing port.
     * 
     * @param <T> type of the port
     * @return an outgoing port
     */
    <T> IOutSimPort<T> createOutPort();
    
    /**
     * Creates a forwarding port.
     * 
     * @param <T> type of the port.
     * @return a forwarding port
     */
    <T> IForwardPort<T> createForwardPort();
    
}
