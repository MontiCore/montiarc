package params;

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
 * @version $Date: 2013-04-05 17:19:31 +0200 (Fri, 05 Apr 2013) $<br>
 * @rev $Revision: 2165 $
 *
 * @type T type of the delayed channel.
 * @param delay constant delay in time units.
 */
component ConstantDelay<T>(int delay) {
  
  timing instant;
  
  port 
    in T portIn,
    out T portOut;
   
}
