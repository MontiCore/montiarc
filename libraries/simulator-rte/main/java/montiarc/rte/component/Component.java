/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

import montiarc.rte.port.InPort;
import montiarc.rte.port.OutPort;
import montiarc.rte.port.Port;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Component {

  String getName();

  /**
   * Simulates this component for as long as there are messages on inputs.
   */
  default void runToCompletion() {
    runToCompletion(0);
  }

  /**
   * Simulates this component for as long as there are messages on inputs.
   *
   * @param simulatedTickLength The simulated time between ticks in nanoseconds.
   */
  void runToCompletion(long simulatedTickLength);

  /**
   * Simulates this component for the specified duration
   *
   * @param ticks The number of ticks to run the simulation for.
   */
  default void run(long ticks) {
    run(ticks, 0);
  }

  /**
   * Simulates this component for the specified duration
   *
   * @param ticks               The number of ticks to run the simulation for.
   * @param simulatedTickLength The simulated time between ticks in nanoseconds.
   */
  void run(long ticks, long simulatedTickLength);

  /**
   * Simulates this component indefinitely
   *
   * @param simulationTickLength The actual length between ticks in nanoseconds
   */
  default void runIndefinitely(long simulationTickLength) {
    runIndefinitely(simulationTickLength, 0);
  }

  /**
   * Simulates this component indefinitely
   *
   * @param simulationTickLength The actual time between ticks in nanoseconds
   * @param simulatedTickLength  The simulated time between ticks in nanoseconds.
   */
  void runIndefinitely(long simulationTickLength, long simulatedTickLength);

  /**
   * @return All incoming ports of this component
   */
  Collection<? extends InPort<?>> getAllInPorts();

  /**
   * @return All outgoing ports of this component
   */
  Collection<? extends OutPort<?>> getAllOutPorts();

  /**
   * @return All ports of this component
   */
  default Collection<? extends Port> getAllPorts() {
    Collection<? extends InPort<?>> inPorts = getAllInPorts();
    Collection<? extends OutPort<?>> outPorts = getAllOutPorts();

    List<Port> allPorts = new ArrayList<>(inPorts.size() + outPorts.size());
    allPorts.addAll(inPorts);
    allPorts.addAll(outPorts);

    return allPorts;
  }
  
}
