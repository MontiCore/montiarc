/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.generic.IStream;

/**
 * Interface for a test port.
 */
public interface ITestPort<T> extends IPort<T> {

  /**
   * @return transmitted stream of this test port.
   */
  IStream<T> getStream();
}
