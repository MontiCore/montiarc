/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.IComponent;
import montiarc.rte.port.ITimeAwareInPort;

import java.util.Collection;

public interface Scheduler {
  void register(IComponent c, Collection<ITimeAwareInPort<?>> inPorts, boolean isSync);
  /** If the component is registered with this scheduler, then it is unregistered. */
  void unregister(IComponent c);

  void requestScheduling(ITimeAwareInPort<?> port, Object newMsg);
  void requestSchedulingOfNewTick(ITimeAwareInPort<?> port);

  void run(IComponent component);
  void run(IComponent component, int ticks);
}
