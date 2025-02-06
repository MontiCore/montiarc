/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import de.se_rwth.commons.logging.Log;
import montiarc.rte.component.Component;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.rte.port.InPort;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class ComponentScheduler {

  protected final Component component;
  protected final Set<InPort<?>> syncPorts;
  protected final Set<InPort<?>> msgEventPorts;
  protected final Set<InPort<?>> allInPorts;

  protected final Deque<InPort<?>> scheduledMsgEventPorts;
  protected boolean isTickScheduled;
  protected boolean isExecuting;

  public ComponentScheduler(Component component,
                            Collection<? extends InPort<?>> msgEventPorts,
                            Collection<? extends InPort<?>> syncPorts) {
    this.component = component;
    this.msgEventPorts = Set.copyOf(msgEventPorts);
    this.syncPorts = Set.copyOf(syncPorts);

    this.allInPorts = new HashSet<>(msgEventPorts.size() + syncPorts.size());
    this.allInPorts.addAll(msgEventPorts);
    this.allInPorts.addAll(syncPorts);

    this.scheduledMsgEventPorts = new ArrayDeque<>(this.msgEventPorts.size());
    this.isTickScheduled = false;
    this.isExecuting = false;
  }

  public void requestScheduling(InPort<?> port, Object newMsg) {
    if (newMsg instanceof Message) {
      throw new IllegalArgumentException("Requested message object should be unwrapped and not instance of the rte class 'Message'");
    }

    if (syncPorts.contains(port)) {
      Log.warn("Scheduler method 'requestScheduling(InPort, Object)' should not be invoked on sync ports.");
    } else {
      requestMsgEventPortScheduling(port);
    }
  }

  public void requestSchedulingOfNewTick(InPort<?> port) {
    requestScheduling(port);
  }

  protected void requestScheduling(InPort<?> port) {
    if (syncPorts.contains(port)) {
      requestSyncPortScheduling();
    } else if (msgEventPorts.contains(port)){
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
    } else if (scheduledMsgEventPorts.isEmpty() && allPortsAreTickBlocked()) {
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
        "running execution is not allowed.");
    }

    if (!scheduledMsgEventPorts.isEmpty()) {
      executePortSchedule(scheduledMsgEventPorts.getFirst());
    } else if (isTickScheduled) {
      executeTickSchedule();
    }
  }

  protected void executePortSchedule(InPort<?> port) {
    if (!scheduledMsgEventPorts.contains(port)) {
      throw new IllegalStateException("Can not execute unscheduled port.");
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
    if (!isTickScheduled) {
      throw new IllegalStateException("Can not execute unscheduled tick.");
    }

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

  void triggerComponentTickPort() {
    this.triggerComponentTickPort(1);
  }

  void triggerComponentTickPort(long ticks) {
    for (int i = 0; i < ticks; i++) {
      this.component.getTickPort().receive(Tick.get());
    }
  }

  public boolean isReadyToExecute() {
    return isTickScheduled || !scheduledMsgEventPorts.isEmpty();
  }

  public void run() {
    while (isReadyToExecute()) {
      executeNextSchedule();
    }
  }

  public void run(int ticks) {
    if (ticks < 0) {
      this.run();
    }

    for (int i = 0; i < ticks && isReadyToExecute(); i++) {
      executeNextSchedule();
    }
  }

  private boolean allPortsHaveBufferedTick() {
    return allInPorts.stream().allMatch(InPort::hasBufferedTick);
  }

  private boolean allPortsAreTickBlocked() {
    return allInPorts.stream().allMatch(InPort::isTickBlocked);
  }
}
