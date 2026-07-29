/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import de.se_rwth.commons.logging.Log;
import montiarc.rte.Simulation;
import montiarc.rte.component.SimComponent;
import montiarc.rte.msg.Message;
import montiarc.rte.oracle.Oracle;
import montiarc.rte.oracle.OracleFactory;
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

  protected Oracle oracle;

  protected final Map<SimComponent, ComponentScheduler> compToScheduler;

  protected boolean requestedToStop = false;

  public CoordinatingScheduler(OracleFactory factory) {
    this.compToScheduler = new HashMap<>();
    this.oracle = factory.createDefaultOracle();
  }

  @Override
  public void register(SimComponent component) {
    ComponentScheduler scheduler;
    if (component.hasModeAutomaton()) {
      scheduler = new ModeComponentScheduler(component, this);
    } else {
      scheduler = new ComponentScheduler(component, this);
    }
    if (Simulation.coordinatingScheduler != this && component.isDelayed()) {
      // Simulation is not currently running -> delayed components are allowed to init before first tick
      scheduler.coordinateNewTick();
    }
    this.compToScheduler.put(component, scheduler);
  }

  @Override
  public void unregister(SimComponent component) {
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

  @Override
  public void runToCompletion(SimComponent component, long simulatedTickLength) {
    run(component, true, Long.MIN_VALUE, 0, simulatedTickLength);
  }

  @Override
  public void runIndefinitely(SimComponent component, long simulationTickLength, long simulatedTickLength) {
    run(component, false, Long.MIN_VALUE, simulationTickLength, simulatedTickLength);
  }

  @Override
  public void runTicks(SimComponent component, long ticks, long simulatedTickLength) {
    run(component, false, ticks, 0, simulatedTickLength);
  }

  /**
   * Method responsible for controlling the simulation
   *
   * @param component            The component to run the simulation for
   * @param runToCompletion      If true all incoming ports are expected to have messages
   *                             and the simulation is run until no more messages can be handled
   * @param ticks                The maximum tick count to run the simulation for.
   *                             If it is {@code Long.MIN_VALUE} the simulation has no upper tick count bound.
   * @param simulationTickLength The actual time the simulation takes for each tick in nanoseconds.
   *                             Can effectively slow the simulation down (e.g. to be interactive)
   * @param simulatedTickLength  The simulated time between ticks in nanoseconds.
   *                             If zero the simulationTickLength will be used.
   *                             If that is also zero the actual computation time for each tick is used.
   */
  public void run(SimComponent component,
                  boolean runToCompletion,
                  long ticks,
                  long simulationTickLength,
                  long simulatedTickLength) {
    if (!compToScheduler.containsKey(component)) {
      throw new IllegalArgumentException("Component not registered");
    }

    requestedToStop = false;
    Simulation.coordinatingScheduler = this;
    Simulation.nanosecondsPerTick = simulatedTickLength <= 0 ? simulationTickLength : simulatedTickLength;
    ComponentScheduler scheduler = compToScheduler.get(component);

    if (!this.isReadyToExecute() && (ticks > 0 || ticks == Long.MIN_VALUE)) {
      this.compToScheduler.values().forEach(ComponentScheduler::coordinateNewTick);
      if (!runToCompletion) {
        scheduler.triggerComponentInPorts();
      }
      Simulation.ticks++;
      Log.info("--- Tick " + Simulation.ticks + " ---", "Scheduler");
      if (ticks != Long.MIN_VALUE) {
        ticks--;
      }
    }

    Collection<ComponentScheduler> activeSchedulers = getActiveSchedulers();
    long tickStart = System.nanoTime();
    while (!activeSchedulers.isEmpty() && !requestedToStop) {

      while (!activeSchedulers.isEmpty()) {
        ComponentScheduler s = oracle.decideAmong(activeSchedulers);
        activeSchedulers.remove(s);
        s.executeNextSchedule();
      }
      activeSchedulers = getActiveSchedulers();

      boolean tickTriggered = false; // End the simulation if after a tick no component can be scheduled
      while (!tickTriggered && activeSchedulers.isEmpty() && (ticks > 0 || ticks == Long.MIN_VALUE) && (!runToCompletion || this.compToScheduler.get(component).allPortsHaveBufferedTick())) {
        // This loop blocks the thread until a component can be scheduled (e.g. through an event outside the simulation)
        // simulationTickLength time has passed
        if (tickStart + simulationTickLength <= System.nanoTime()) {
          this.compToScheduler.values().forEach(ComponentScheduler::coordinateNewTick);
          if (!runToCompletion) {
            scheduler.triggerComponentInPorts();
          }
          tickTriggered = true;
          Simulation.ticks++;
          Log.info("--- Tick " + Simulation.ticks + " ---", "Scheduler");
          if (ticks != Long.MIN_VALUE) {
            ticks--;
          }
          if (simulationTickLength > 0 && tickStart + simulationTickLength + 10000000 <= System.nanoTime()) {
            Log.warn("Can't keep up! The simulation of one tick took " + ((System.nanoTime() - tickStart + simulationTickLength) / 1000000000) + " seconds longer than the set tick length");
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

  public boolean isASubCompScheduled(SimComponent comp) {
    Collection<? extends SimComponent> directSubs = comp.getAllSubcomponents();

    return
      directSubs.stream().map(this.compToScheduler::get)
        .anyMatch(ComponentScheduler::isReadyToExecute)
        || directSubs.stream().anyMatch(this::isASubCompScheduled);
  }
}
