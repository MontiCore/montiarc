/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.Component;
import montiarc.rte.port.InPort;

import java.util.Collection;

public interface Scheduler {
  void register(Component c, Collection<? extends InPort<?>> inPorts, boolean isSync);
  /** If the component is registered with this scheduler, then it is unregistered. */
  void unregister(Component c);

  void requestScheduling(InPort<?> port, Object newMsg);
  void requestSchedulingOfNewTick(InPort<?> port);

  void run(Component component);
  void run(Component component, int ticks);
}
