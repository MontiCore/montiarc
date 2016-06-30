/**
 * 
 */
package sim.error;

/**
 * Is thrown, if parts of the simulation are not implemented yet.
 *
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $LastChangedBy: ahaber $
 * @version $LastChangedDate: 2012-11-14 15:43:58 +0100 (Mi, 14 Nov 2012) $<br>
 *          $LastChangedRevision: 1686 $
 */
public class NotImplementedYetException extends RuntimeException {

    /**
     * @param message exception message
     */
    public NotImplementedYetException(final String message) {
        super(message);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -2621245120182244300L;
    
}
