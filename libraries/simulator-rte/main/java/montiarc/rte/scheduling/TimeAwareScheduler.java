/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.ITimeAwareInPort;

import java.util.Collection;

public interface TimeAwareScheduler {
  void register(ITimedComponent c, Collection<ITimeAwareInPort<?>> inPorts, boolean isSync);
  /** If the component is registered with this scheduler, then it is unregistered. */
  void unregister(ITimedComponent c);

  void requestScheduling(ITimeAwareInPort<?> port, Object newMsg);
  void requestSchedulingOfNewTick(ITimeAwareInPort<?> port);

  void run(ITimedComponent component);
  void run(ITimedComponent component, int ticks);
}
