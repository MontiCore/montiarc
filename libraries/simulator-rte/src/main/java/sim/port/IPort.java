/**
 * 
 */
package sim.port;

/**
 * A common port interface.
 *
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $LastChangedBy: ahaber $
 * @version $LastChangedDate: 2013-04-05 10:19:04 +0200 (Fr, 05 Apr 2013) $<br>
 *          $LastChangedRevision: 2147 $
 * @param <T> data type of this port
 */
public interface IPort<T> extends IInSimPort<T>, IOutSimPort<T> {
    
}
