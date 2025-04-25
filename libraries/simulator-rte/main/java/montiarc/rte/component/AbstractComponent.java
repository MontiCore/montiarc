/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

import de.se_rwth.commons.logging.Log;
import montiarc.rte.behavior.Behavior;
import montiarc.rte.logging.Aspects;
import montiarc.rte.logging.DataFormatter;
import montiarc.rte.port.InOutPort;
import montiarc.rte.port.InPort;
import montiarc.rte.port.NoMsgType;
import montiarc.rte.port.OutPort;
import montiarc.rte.port.TickPort;
import montiarc.rte.scheduling.Scheduler;

import java.util.List;
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
public abstract class AbstractComponent<I, B extends Behavior<I>> implements Component {

  protected final String name;
  protected final InOutPort<NoMsgType, NoMsgType> tickPort;
  protected Set<OutPort<?>> unconnectedOutputs;
  protected final Scheduler scheduler;

  protected boolean isAtomic;
  protected B behavior;

  protected boolean initialized;

  protected AbstractComponent(String name, Scheduler scheduler) {
    this.name = name;
    this.scheduler = scheduler;
    this.tickPort = new TickPort(this, scheduler);
    this.initialized = false;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public InPort<NoMsgType> getTickPort() {
    return this.tickPort;
  }

  public Scheduler getScheduler() {
    return this.scheduler;
  }

  public B getBehavior() {
    return this.behavior;
  }

  /**
   * Runs the simulation
   */
  public void run() {
    ensureInitialized();
    this.scheduler.runToCompletion(this);
  }

  /**
   * Runs the simulation for the specified duration
   * @param ticks The number of ticke to run the simulation for.
   */
  public void run(long ticks) {
    ensureInitialized();
    this.scheduler.runTicks(this, ticks);
  }

  public void unregisterFromScheduler() {
    this.scheduler.unregister(this);
  }

  public abstract List<OutPort<?>> getAllOutPorts();
  protected abstract List<InOutPort<?, ?>> getAllSyncedInPorts();
  protected abstract Object portValueOf(InPort<?> p);
  protected abstract List<OutPort<?>> getAllDelayedOutPorts();

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

  protected void sendTickOnAllDelayedOutputs() {
    for (OutPort<?> outP : this.getAllDelayedOutPorts()) {
      outP.sendTick();
    }
  }

  protected void ensureInitialized() {
    if (!initialized) {
      init();
    }
  }

  @Override
  public void init() {
    if (initialized) {
      Log.info(() -> "Component already initialized", this.getName() + "#init");
      return;
    }
    this.initialized = true;

    if (this.isAtomic) {
      if (behavior != null) {
        behavior.init();
      }
      sendTickOnAllDelayedOutputs();
    } else {
      for (Component comp : this.getAllSubcomponents()) {
        comp.init();
      }
    }
    tickPort.sendTick();
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
}
