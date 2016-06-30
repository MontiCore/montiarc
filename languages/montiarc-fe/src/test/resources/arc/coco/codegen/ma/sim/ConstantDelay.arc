package ma.sim;

/**
 * @brief adds a fix delay to a channel.
 * 
 * All messages received on port {@link portIn} are relayed
 * to {@link portOut} after a fix delay of {@code delay} time
 * units.
 * 
 * <br>
 * <br>
 * Copyright (c) ${year} RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Date: 2015-01-19 12:45:03 +0100 (Mo, 19 Jan 2015) $<br>
 * @rev $Revision: 3080 $
 *
 * @type T type of the delayed channel.
 * @param delay constant delay in time units.
 */
component ConstantDelay<T>(int delay) {
  
  timing delayed;
  
  port 
    in T portIn,
    out T portOut;
   
}
