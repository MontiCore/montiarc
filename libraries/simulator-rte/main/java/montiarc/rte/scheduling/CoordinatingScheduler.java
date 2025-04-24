/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.lang.Simulation;
import montiarc.rte.component.Component;
import montiarc.rte.msg.Message;
import montiarc.rte.port.InPort;

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

  protected final Map<Component, ComponentScheduler> compToScheduler;

  protected boolean requestedToStop = false;

  public CoordinatingScheduler() {
    this.compToScheduler = new HashMap<>();
  }

  @Override
  public void register(Component component,
                       Collection<? extends InPort<?>> msgEventPorts,
                       Collection<? extends InPort<?>> syncPorts) {
    if (component.hasModeAutomaton()) {
      this.compToScheduler.put(component, new ModeComponentScheduler(component, msgEventPorts, syncPorts, this));
    } else {
      this.compToScheduler.put(component, new ComponentScheduler(component, msgEventPorts, syncPorts));
    }
  }

  @Override
  public void unregister(Component component) {
    this.compToScheduler.remove(component);
  }

  @Override
  public void requestScheduling(InPort<?> port, Object newMsg) {
    if (newMsg instanceof Message) {
      throw new IllegalArgumentException("Requested message object should be unwrapped and not instance of the rte class 'Message'");
    }

    this.compToScheduler.get(port.getOwner()).requestScheduling(port, newMsg);
  }

  @Override
  public void requestSchedulingOfNewTick(InPort<?> port) {
    this.compToScheduler.get(port.getOwner()).requestSchedulingOfNewTick(port);
  }

  private Collection<ComponentScheduler> getActiveSchedulers() {
    return this.compToScheduler.values().stream()
      .filter(ComponentScheduler::isReadyToExecute)
      .collect(Collectors.toList());
  }

  public void runToCompletion(Component component) {
    run(component, true, Long.MIN_VALUE, 0);
  }

  public void runIndefinitely(Component component) {
    run(component, false, Long.MIN_VALUE, 0);
  }

  public void runTicks(Component component, long ticks) {
    run(component, false, ticks, 0);
  }

  /**
   * Method responsible for controlling the simulation
   *
   * @param component            The component to run the simulation for
   * @param runToCompletion      If true all incoming ports are expected to have messages
   *                             and the simulation is run until no more messages can be handled
   * @param ticks                The maximum tick count to run the simulation for.
   *                             If it is {@code Long.MIN_VALUE} the simulation has no upper tick count bound.
   * @param simulationTickLength The time the simulation takes for each tick.
   *                             Can effectively slow the simulation down (e.g. to be interactive)
   */
  protected void run(Component component, boolean runToCompletion, long ticks, long simulationTickLength) {
    if (!compToScheduler.containsKey(component)) {
      throw new IllegalArgumentException("Component not registered");
    }

    requestedToStop = false;
    Simulation.coordinatingScheduler = this;
    ComponentScheduler scheduler = compToScheduler.get(component);

    if (!this.isReadyToExecute() && (ticks > 0 || ticks == Long.MIN_VALUE)) {
      if (runToCompletion) {
        scheduler.triggerComponentTickPort();
      } else {
        scheduler.triggerComponentInPorts();
      }
      if (ticks != Long.MIN_VALUE) {
        ticks--;
      }
    }

    Collection<ComponentScheduler> activeSchedulers = getActiveSchedulers();
    long tickStart = System.nanoTime();
    while (!activeSchedulers.isEmpty() && !requestedToStop) {
      for (ComponentScheduler s : activeSchedulers) {
        s.executeNextSchedule();
      }

      activeSchedulers = getActiveSchedulers();
      boolean tickTriggered = false; // End the simulation if after a tick no component can be scheduled
      while (!tickTriggered && activeSchedulers.isEmpty() && (ticks > 0 || ticks == Long.MIN_VALUE)) {
        // This loop blocks the thread until a component can be scheduled (e.g. through an event outside the simulation)
        // simulationTickLength time has passed
        if (tickStart + simulationTickLength <= System.nanoTime()) {
          if (runToCompletion) {
            scheduler.triggerComponentTickPort();
          } else {
            scheduler.triggerComponentInPorts();
          }
          tickTriggered = true;
          Simulation.ticks++;
          if (ticks != Long.MIN_VALUE) {
            ticks--;
          }
          tickStart = System.nanoTime();
        }
        activeSchedulers = getActiveSchedulers();
      }
    }
    Simulation.coordinatingScheduler = null;
  }

  public void stop() {
    requestedToStop = true;
  }

  private boolean isReadyToExecute() {
    return this.compToScheduler.values().stream().anyMatch(ComponentScheduler::isReadyToExecute);
  }

  public boolean isASubCompScheduled(Component comp) {
    Collection<? extends Component> directSubs = comp.getAllSubcomponents();

    return
      directSubs.stream().map(this.compToScheduler::get)
        .anyMatch(ComponentScheduler::isReadyToExecute)
        || directSubs.stream().anyMatch(this::isASubCompScheduled);
  }
}
