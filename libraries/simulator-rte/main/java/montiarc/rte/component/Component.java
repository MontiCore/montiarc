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
   * @deprecated Use {@link Component#runToCompletion()} instead.
   */
  @Deprecated
  default void run() {
    runToCompletion();
  }

  /**
   * Simulates this component for as long as there are messages on inputs.
   */
  void runToCompletion();

  /**
   * Simulates this component for the specified duration
   * @param ticks The number of ticks to run the simulation for.
   */
  void run(long ticks);

  /**
   * Simulates this component indefinitely
   *
   * @param simulationTickLength the length between ticks in nanoseconds
   */
  void runIndefinitely(long simulationTickLength);

  /** @return All incoming ports of this component */
  Collection<? extends InPort<?>> getAllInPorts();

  /** @return All outgoing ports of this component */
  Collection<? extends OutPort<?>> getAllOutPorts();

  /** @return All ports of this component */
  default Collection<? extends Port> getAllPorts() {
    Collection<? extends InPort<?>> inPorts = getAllInPorts();
    Collection<? extends OutPort<?>> outPorts = getAllOutPorts();

    List<Port> allPorts = new ArrayList<>(inPorts.size() + outPorts.size());
    allPorts.addAll(inPorts);
    allPorts.addAll(outPorts);

    return allPorts;
  }
  
  @Deprecated(forRemoval = true)
  void init();
}
