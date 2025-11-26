/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.SimComponent;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.rte.port.InPort;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Set;

public class ComponentScheduler {

  protected final CoordinatingScheduler coordinator;
  protected final SimComponent component;
  protected final Set<InPort<?>> syncPorts;
  protected final Set<InPort<?>> msgEventPorts;
  protected final Set<InPort<?>> allInPorts;

  protected final Deque<InPort<?>> scheduledMsgEventPorts;
  protected boolean isTickScheduled;
  protected boolean canExecuteTick;
  protected boolean isExecuting;

  public ComponentScheduler(SimComponent component, CoordinatingScheduler coordinator) {
    this.coordinator = coordinator;
    this.component = component;
    this.msgEventPorts = Set.copyOf(component.getAllMsgEventInPorts());
    this.syncPorts = Set.copyOf(component.getAllSyncedInPorts());

    this.allInPorts = new LinkedHashSet<>(msgEventPorts.size() + syncPorts.size());
    this.allInPorts.addAll(msgEventPorts);
    this.allInPorts.addAll(syncPorts);

    this.scheduledMsgEventPorts = new ArrayDeque<>(this.msgEventPorts.size());
    this.isTickScheduled = false;
    this.isExecuting = false;
    this.canExecuteTick = false;

    if (allInPorts.isEmpty()) {
      orderTickSchedule();
    }
  }

  public void requestScheduling(InPort<?> port, Object newMsg) {
    if (newMsg instanceof Message) {
      throw new IllegalArgumentException("Requested message object should be unwrapped and not instance of the rte class 'Message'" );
    }

    if (!syncPorts.contains(port)) {
      requestMsgEventPortScheduling(port);
    }
  }

  public void requestSchedulingOfNewTick(InPort<?> port) {
    requestScheduling(port);
  }

  protected void requestScheduling(InPort<?> port) {
    if (syncPorts.contains(port)) {
      requestSyncPortScheduling();
    } else if (msgEventPorts.contains(port)) {
      requestMsgEventPortScheduling(port);
    } else {
      throw new IllegalArgumentException(
        String.format("Port '%s'is not registered with the scheduler for component '%s'.",
          port.getQualifiedName(), component.getName())
      );
    }
  }

  protected void requestSyncPortScheduling() {
    if (this.isExecuting) {
      return;  // After execution has finished, the scheduler will schedule the port by itself if there is a new message
    }

    if (isTickScheduled) {
      return;
    } else if (allPortsHaveBufferedTick()) {
      orderTickSchedule();
    }
  }

  protected void requestMsgEventPortScheduling(InPort<?> port) {
    if (this.isExecuting) {
      return;  // After execution has finished, the scheduler will schedule the port by itself if there is a new message
    }

    if (scheduledMsgEventPorts.contains(port) || port.isBufferEmpty()) {
      return;
      // Unless the port has messages _before_ the next tick
    }

    if (!port.isTickBlocked()) {
      scheduledMsgEventPorts.add(port);
    } else if (scheduledMsgEventPorts.isEmpty() && allPortsHaveBufferedTick()) {
      orderTickSchedule();
    }
  }

  /**
   * Forces all sync ports to execute {@link InPort#dropMessagesIgnoredBySync()}
   * and sets {@link ComponentScheduler#isTickScheduled} to true.
   */
  protected void orderTickSchedule() {
    for (InPort<?> p : syncPorts) {
      p.dropMessagesIgnoredBySync();
    }
    isTickScheduled = true;
  }

  public void executeNextSchedule() {
    if (isExecuting) {
      throw new IllegalStateException("Triggering the execution of a component that has not finished an already " +
        "running execution is not allowed." );
    }

    if (!component.isInitialized() && (!component.isDelayed() || canExecuteTick)) {
      executeInitSchedule();
    } else if (!scheduledMsgEventPorts.isEmpty()) {
      executePortSchedule(scheduledMsgEventPorts.getFirst());
    } else if (isTickScheduled) {
      executeTickSchedule();
    }
  }

  protected void executePortSchedule(InPort<?> port) {
    if (!scheduledMsgEventPorts.contains(port)) {
      throw new IllegalStateException("Cannot execute unscheduled port." );
    }

    isExecuting = true;
    scheduledMsgEventPorts.remove(port);
    component.handleMessage(port);
    port.pollBuffer();  // Remove processed message from port buffer
    isExecuting = false;

    if (!port.isBufferEmpty()) {
      this.requestMsgEventPortScheduling(port);
    }
  }

  protected void executeTickSchedule() {
    if (!isTickScheduled || !canExecuteTick) {
      throw new IllegalStateException("Cannot execute unscheduled tick." );
    }

    canExecuteTick = false;
    isExecuting = true;
    isTickScheduled = false;
    component.handleTick();
    for (InPort<?> p : allInPorts) {
      removeCurrentTimeSliceContentFrom(p);
    }
    isExecuting = false;

    // Put ports that had messages buffered behind the tick into the scheduling queue again
    for (InPort<?> p : msgEventPorts) {
      if (!p.isBufferEmpty()) {
        this.requestMsgEventPortScheduling(p);
      }
    }

    if (allPortsHaveBufferedTick()) {
      requestSyncPortScheduling();
    }

    if (allInPorts.isEmpty()) {
      orderTickSchedule();
    }
  }

  protected void executeInitSchedule() {
    if (component.isInitialized()) {
      throw new IllegalStateException("Cannot execute init schedule for initialized component." );
    }

    // If delayed treat init as tick
    if (component.isDelayed()) {
      canExecuteTick = false;
      isTickScheduled = false;
    }

    isExecuting = true;
    component.init();
    isExecuting = false;

    if (component.isDelayed()) {
      // Put ports that had messages buffered behind the tick into the scheduling queue again
      for (InPort<?> p : msgEventPorts) {
        if (!p.isBufferEmpty()) {
          this.requestMsgEventPortScheduling(p);
        }
      }

      if (allPortsHaveBufferedTick()) {
        requestSyncPortScheduling();
      }

      if (allInPorts.isEmpty()) {
        orderTickSchedule();
      }
    }
  }

  /**
   * Removes all messages until and including the next tick from the port's buffer.
   */
  private void removeCurrentTimeSliceContentFrom(InPort<?> p) {
    while (!p.isBufferEmpty() && !p.isTickBlocked()) {
      p.pollBuffer();  // Remove all data messages
    }
    if (!p.isBufferEmpty() && p.isTickBlocked()) {
      p.pollBuffer();  // Remove the time-slice-ending tick
    }
  }

  void triggerComponentInPorts() {
    this.component.getAllInPorts().forEach(p -> p.receive(Tick.get()));
  }

  public boolean isReadyToExecute() {
    return (isTickScheduled && canExecuteTick) || !scheduledMsgEventPorts.isEmpty() || (!component.isInitialized() && (!component.isDelayed() || canExecuteTick));
  }

  protected void coordinateNewTick() {
    this.canExecuteTick = true;
  }

  protected boolean allPortsHaveBufferedTick() {
    return allInPorts.stream().allMatch(InPort::hasBufferedTick);
  }

  private boolean allPortsAreTickBlocked() {
    return allInPorts.stream().allMatch(InPort::isTickBlocked);
  }
}
