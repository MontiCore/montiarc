/* (c) https://github.com/MontiCore/monticore */
/*
 *
 * http://www.se-rwth.de/
 */
package sim.comp;

import sim.comp.gen.AMessageAbstraction;
import sim.port.IInPort;

/**
 * Implementation of a message abstraction component.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Revision: 3110 $,
 *          $Date: 2015-02-02 14:49:50 +0100 (Mo, 02 Feb 2015) $
 * @since   2.4.0
 *
 */
public class MessageAbstractionImpl<T> extends AMessageAbstraction<T> {

    /**
     * @see sim.comp.gen.AMessageAbstraction#treatTimeAndDataIn(java.lang.Object)
     */
    @Override
    protected void treatTimeAndDataIn(T message) {
        // do nothing
    }

    /**
     * @see sim.generic.ATimedSingleIn#timeStep()
     */
    @Override
    protected void timeStep() {
        // do nothing, time is automatically forwarded
    }

    /**
     * @see sim.comp.gen.interfaces.IMessageAbstraction#_getSourceTickPort()
     */
    @Override
    public IInPort<T> _getSourceTickPort() {
        return getTimeAndDataIn();
    }
    
    
    
}
