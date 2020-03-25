/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.generic.IStream;

/**
 * Interface for a test port.
 *
 *          $Date: 2014-03-25 15:02:56 +0100 (Di, 25 Mrz 2014) $
 *
 */
public interface ITestPort<T> extends IPort<T> {
    
    /**
     * @return transmitted stream of this test port.
     */
    IStream<T> getStream();
}
