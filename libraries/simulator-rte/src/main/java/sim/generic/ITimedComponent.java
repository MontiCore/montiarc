/**
 * 
 */
package sim.generic;

/**
 * A timed component.
 *
 * <br>
 * <br>
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Date: 2013-06-14 13:05:53 +0200 (Fr, 14 Jun 2013) $<br>
 *          $Revision: 2384 $
 * @since   2.3.0 (14.06.2013)
 */
public interface ITimedComponent extends IComponent {
    
    /**
     * @return the local time from the component
     */
    int getLocalTime();
}
