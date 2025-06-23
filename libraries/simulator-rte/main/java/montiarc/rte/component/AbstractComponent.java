/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.rte.behavior.Behavior;
import montiarc.rte.logging.Aspects;
import montiarc.rte.logging.DataFormatter;
import montiarc.rte.oracle.Oracle;
import montiarc.rte.oracle.OracleOwner;
import montiarc.rte.port.InOutPort;
import montiarc.rte.port.InPort;
import montiarc.rte.port.OutPort;
import montiarc.rte.scheduling.Scheduler;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Provides basic implementation for timed components, especially including message handling:
 * {@link #handleTick()}, {@link #handleMessage(InPort)} and {@link #handleTickExecution()}.
 * <br>
 * Components should provide logic for the method {@link #handleMessageWithBehavior(InPort)} to call the
 * behavior for the respective port event.
 * <br>
 * They should also provide logic for the method {@link #buildSyncMessage()}, creating a synced input object of
 * type {@code <I>} with the current values of all synchronized input ports.
 * <br>
 * For dynamic components with modes, {@link AbstractModeComponent} should be used.
 *
 * @param <I> the type of the synced input message class
 *            (containing a message for every port at the time of a synced tick)
 * @param <B> the class defining the interface of the behavior (accepting tick and message events)
 */
public abstract class AbstractComponent<I, B extends Behavior<I>>
  implements SimComponent, OracleOwner {

  protected final String name;
  protected Set<OutPort<?>> unconnectedOutputs;
  protected final Scheduler scheduler;
  protected Oracle oracle;
  protected SimComponent superComponent;

  protected boolean isAtomic;
  protected B behavior;

  protected boolean initialized;

  protected AbstractComponent(String name, Scheduler scheduler) {
    this.name = name;
    this.scheduler = scheduler;
    this.initialized = false;
  }

  @Override
  public String getName() {
    return name;
  }

  protected Scheduler getScheduler() {
    return this.scheduler;
  }

  // TODO: add this to SimComponent and add override annotation
  public void setOracle(Oracle oracle) {
    Preconditions.checkNotNull(oracle);
    this.oracle = oracle;
  }

  @Override
  public Oracle getOracle() {
    return this.oracle;
  }

  protected B getBehavior() {
    return this.behavior;
  }

  public void setSuperComponent(SimComponent superComponent) {
    this.superComponent = superComponent;
  }

  @Override
  public Optional<SimComponent> getSuperComponent() {
    return Optional.ofNullable(this.superComponent);
  }

  @Override
  public void runToCompletion(long simulatedTickLength) {
    this.scheduler.runToCompletion(this, simulatedTickLength);
  }

  @Override
  public void run(long ticks, long simulatedTickLength) {
    this.scheduler.runTicks(this, ticks, simulatedTickLength);
  }

  @Override
  public void runIndefinitely(long simulationTickLength, long simulatedTickLength) {
    this.scheduler.runIndefinitely(this, simulationTickLength, simulatedTickLength);
  }

  @Override
  public void unregisterFromScheduler() {
    this.scheduler.unregister(this);
  }

  protected abstract Object portValueOf(InPort<?> p);

  @Override
  public abstract List<? extends InOutPort<?, ?>> getAllSyncedInPorts();

  @Override
  public abstract List<? extends InOutPort<?, ?>> getAllInPorts();

  protected void sendTickOnAllOutputs() {
    for (OutPort<?> outP : this.getAllOutPorts()) {
      outP.sendTick();
    }
  }

  protected void sendTickOnAllUnconnectedOutputs() {
    for (OutPort<?> outP : this.unconnectedOutputs) {
      outP.sendTick();
    }
  }

  @Override
  public boolean isInitialized() {
    return this.initialized;
  }

  @Override
  public void init() {
    if (initialized) {
      Log.info(() -> "Component already initialized", this.getName() + "#" + Aspects.INIT);
      return;
    }
    Log.info(() -> "", this.getName() + "#" + Aspects.INIT);
    this.initialized = true;

    if (this.isAtomic) {
      if (behavior != null) {
        behavior.init();
      }
      if (isDelayed()) {
        sendTickOnAllOutputs();
      }
    }
  }

  @Override
  public void handleTick() {
    Log.info(() -> DataFormatter.TK, this.getName() + "#" + Aspects.RECEIVE_EVENT);
    handleTickExecution();
  }

  @Override
  public final void handleMessage(InPort<?> p) {
    Log.info(() -> DataFormatter.format(portValueOf(p)), this.getName() + "#" + Aspects.RECEIVE_EVENT);
    this.processMessage(p);
  }

  protected void processMessage(InPort<?> p) {
    if (!isAtomic) {
      ((montiarc.rte.port.InOutPort<?, ?>) p).forwardWithoutRemoval();
    } else if (behavior != null) {
      handleMessageWithBehavior(p);
    }
  }

  protected abstract void handleMessageWithBehavior(InPort<?> p);

  protected abstract I buildSyncMessage();

  protected void handleTickExecution() {
    // forward message to sub components / atomic behavior
    if (isAtomic) {
      if (behavior != null) {
        this.behavior.tick(buildSyncMessage());
      }
      sendTickOnAllOutputs();

    } else {
      // Component is decomposed
      for (InOutPort<?, ?> p : getAllSyncedInPorts()) {
        // If there was a message on the port, forward it
        if (!p.isTickBlocked()) {
          p.forwardWithoutRemoval();
        }
      }

      for (InOutPort<?, ?> p : this.getAllInPorts()) {
        // "Forward" the tick of this execution
        // (Pure forwarding does not work for sync ports, as we did not remove the message before it.
        //  This is done by the scheduler later. Therefore, send the tick manually.)
        p.sendTick();
      }

      // Output ports that have no internal connection do not receive ticks via internal behavior or
      // connectors. Therefore, we have to send ticks to them manually.
      this.sendTickOnAllUnconnectedOutputs();
    }
  }

  @Override
  public boolean isDelayed() {
    return this.behavior != null && this.behavior.isDelayed();
  }
}
