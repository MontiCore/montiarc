/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package sim.port;

/**
 * {@link IPortFactory} that produces {@link TestPort} objects.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Revision: 2802 $,
 *          $Date: 2014-03-25 15:02:56 +0100 (Di, 25 Mrz 2014) $
 * @since   2.5.0
 *
 */
public class TestPortFactory implements IPortFactory {

    /**
     * @see sim.port.IPortFactory#createInPort()
     */
    @Override
    public <T> IInSimPort<T> createInPort() {
        return new TestPort<T>();
    }

    /**
     * @see sim.port.IPortFactory#createOutPort()
     */
    @Override
    public <T> IOutSimPort<T> createOutPort() {
        return new TestPort<T>();
    }

    /**
     * @see sim.port.IPortFactory#createForwardPort()
     */
    @Override
    public <T> IForwardPort<T> createForwardPort() {
        return new TestForwardPort<T>();
    }
    
    
    
}
