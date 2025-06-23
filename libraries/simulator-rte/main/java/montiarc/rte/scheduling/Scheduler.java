/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.SimComponent;
import montiarc.rte.port.InPort;

public interface Scheduler {

  void register(SimComponent c);

  /** If the component is registered with this scheduler, then it is unregistered. */
  void unregister(SimComponent c);

  void requestScheduling(InPort<?> port, Object newMsg);

  void requestSchedulingOfNewTick(InPort<?> port);

  void runToCompletion(SimComponent component, long simulatedTickLength);

  /**
   * Run the simulation indefinitely
   *
   * @param component            the component to start the simulation with
   * @param simulationTickLength the length between ticks in nanoseconds
   */
  void runIndefinitely(SimComponent component, long simulationTickLength, long simulatedTickLength);

  void runTicks(SimComponent component, long ticks, long simulatedTickLength);
}
