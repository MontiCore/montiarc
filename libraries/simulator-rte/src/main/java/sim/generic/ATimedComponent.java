/**
 * 
 */
package sim.generic;


/**
 * Abstract implementation of a timed component.
 *
 * <br>
 * <br>
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Date: 2015-02-02 14:49:50 +0100 (Mo, 02 Feb 2015) $<br>
 *          $Revision: 3110 $
 * @since   2.3.0 (14.06.2013)
 */
public abstract class ATimedComponent extends AComponent implements ITimedComponent {
    
    /** The local time of this component. */
    private int localTime;
    
    /**
     * 
     */
    public ATimedComponent() {
        super();
        localTime = 0;
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IComponent#getLocalTime()
     */
    @Override
    public int getLocalTime() {
        return localTime;
    }
    
    /**
     * Increments the components time by one.
     */
    protected final void incLocalTime() {
        localTime += 1;
    }

    /**
     * This method is called at the start of a new time slice and may be used to
     * realize time triggered behavior.
     */
    protected abstract void timeStep();
    

}
