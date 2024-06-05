/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.msg.Message;
import montiarc.rte.port.ITimeAwareInPort;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A central scheduler for a hierarchy of components.
 * Defers to individual {@link ComponentScheduler}s for
 * individual component processing
 */
public class CoordinatingScheduler implements TimeAwareScheduler {

  private final Map<ITimedComponent, ComponentScheduler> compToScheduler;

  public CoordinatingScheduler() {
    this.compToScheduler = new HashMap<>();
  }

  @Override
  public void register(ITimedComponent component, Collection<ITimeAwareInPort<?>> inPorts, boolean isSync) {
    if (component.hasModeAutomaton()) {
      this.compToScheduler.put(component, new ModeComponentScheduler(component, inPorts, isSync, this));
    } else {
      this.compToScheduler.put(component, new ComponentScheduler(component, inPorts, isSync));
    }
  }

  @Override
  public void unregister(ITimedComponent component) {
    this.compToScheduler.remove(component);
  }

  @Override
  public void requestScheduling(ITimeAwareInPort<?> port, Object newMsg) {
    if (newMsg instanceof Message) {
      throw new IllegalArgumentException("Requested message object should be unwrapped and not instance of the rte class 'Message'");
    }

    this.compToScheduler.get(port.getOwner()).requestScheduling(port, newMsg);
  }

  @Override
  public void requestSchedulingOfNewTick(ITimeAwareInPort<?> port) {
    this.compToScheduler.get(port.getOwner()).requestSchedulingOfNewTick(port);
  }

  @Override
  public void run(ITimedComponent component) {
    if (!compToScheduler.containsKey(component)) {
      throw new IllegalArgumentException("Component not registered");
    }

    ComponentScheduler scheduler = compToScheduler.get(component);

    if (!this.isReadyToExecute()) {
      scheduler.triggerComponentTickPort();
    }

    Collection<ComponentScheduler> activeSchedulers =
      compToScheduler.values().stream()
        .filter(ComponentScheduler::isReadyToExecute)
        .collect(Collectors.toList());

    while (!activeSchedulers.isEmpty()) {
      activeSchedulers.forEach(ComponentScheduler::executeNextSchedule);

      activeSchedulers =
        compToScheduler.values().stream()
          .filter(ComponentScheduler::isReadyToExecute)
          .collect(Collectors.toList());

      if (activeSchedulers.isEmpty()) {
        scheduler.triggerComponentTickPort();
        activeSchedulers =
          compToScheduler.values().stream()
            .filter(ComponentScheduler::isReadyToExecute)
            .collect(Collectors.toList());
      }
    }
  }

  public void run(ITimedComponent component, int ticks) {
    if (!compToScheduler.containsKey(component)) {
      throw new IllegalArgumentException("Component not registered");
    }

    compToScheduler.get(component).triggerComponentTickPort(ticks);

    Collection<ComponentScheduler> activeSchedulers =
      compToScheduler.values().stream()
      .filter(ComponentScheduler::isReadyToExecute)
      .collect(Collectors.toList());

    while (!activeSchedulers.isEmpty()) {
      activeSchedulers.forEach(ComponentScheduler::executeNextSchedule);

      activeSchedulers =
        compToScheduler.values().stream()
          .filter(ComponentScheduler::isReadyToExecute)
          .collect(Collectors.toList());
    }
  }

  private boolean isReadyToExecute() {
    return this.compToScheduler.values().stream().anyMatch(ComponentScheduler::isReadyToExecute);
  }

  boolean isASubCompScheduled(ITimedComponent comp) {
    Collection<? extends ITimedComponent> directSubs = comp.getAllSubcomponents();

    return
      directSubs.stream().map(this.compToScheduler::get)
        .anyMatch(ComponentScheduler::isReadyToExecute)
      || directSubs.stream().anyMatch(this::isASubCompScheduled);
  }
}
