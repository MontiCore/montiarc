/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.IComponent;
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
public class CoordinatingScheduler implements Scheduler {

  private final Map<IComponent, ComponentScheduler> compToScheduler;

  public CoordinatingScheduler() {
    this.compToScheduler = new HashMap<>();
  }

  @Override
  public void register(IComponent component, Collection<ITimeAwareInPort<?>> inPorts, boolean isSync) {
    if (component.hasModeAutomaton()) {
      this.compToScheduler.put(component, new ModeComponentScheduler(component, inPorts, isSync, this));
    } else {
      this.compToScheduler.put(component, new ComponentScheduler(component, inPorts, isSync));
    }
  }

  @Override
  public void unregister(IComponent component) {
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

  private Collection<ComponentScheduler> getActiveSchedulers() {
    return this.compToScheduler.values().stream()
      .filter(ComponentScheduler::isReadyToExecute)
      .collect(Collectors.toList());
  }

  @Override
  public void run(IComponent component) {
    if (!compToScheduler.containsKey(component)) {
      throw new IllegalArgumentException("Component not registered");
    }

    ComponentScheduler scheduler = compToScheduler.get(component);

    if (!this.isReadyToExecute()) {
      scheduler.triggerComponentTickPort();
    }

    Collection<ComponentScheduler> activeSchedulers = getActiveSchedulers();
    while (!activeSchedulers.isEmpty()) {
      for (ComponentScheduler s : activeSchedulers) {
        s.executeNextSchedule();
      }

      activeSchedulers = getActiveSchedulers();
      if (activeSchedulers.isEmpty()) {
        scheduler.triggerComponentTickPort();
        activeSchedulers = getActiveSchedulers();
      }
    }
  }

  public void run(IComponent component, int ticks) {
    if (!compToScheduler.containsKey(component)) {
      throw new IllegalArgumentException("Component not registered");
    }

    compToScheduler.get(component).triggerComponentTickPort(ticks);

    Collection<ComponentScheduler> activeSchedulers = getActiveSchedulers();

    while (!activeSchedulers.isEmpty()) {
      for (ComponentScheduler s : activeSchedulers) {
        s.executeNextSchedule();
      }
      activeSchedulers = getActiveSchedulers();
    }
  }

  private boolean isReadyToExecute() {
    return this.compToScheduler.values().stream().anyMatch(ComponentScheduler::isReadyToExecute);
  }

  boolean isASubCompScheduled(IComponent comp) {
    Collection<? extends IComponent> directSubs = comp.getAllSubcomponents();

    return
      directSubs.stream().map(this.compToScheduler::get)
        .anyMatch(ComponentScheduler::isReadyToExecute)
      || directSubs.stream().anyMatch(this::isASubCompScheduled);
  }
}
