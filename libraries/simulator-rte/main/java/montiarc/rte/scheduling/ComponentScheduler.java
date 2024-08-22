/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.Component;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.rte.port.InPort;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Set;

public class ComponentScheduler {

  protected final Component component;
  protected final Set<InPort<?>> allInPorts;
  protected final boolean isSyncComp;

  protected final Deque<InPort<?>> scheduledPorts;
  protected boolean isTickScheduled;
  protected boolean isExecuting;


  public ComponentScheduler(Component component, Collection<? extends InPort<?>> inPorts, boolean isSync) {
    this.component = component;
    this.allInPorts = Set.copyOf(inPorts);
    this.isSyncComp = isSync;

    this.scheduledPorts = new ArrayDeque<>(this.allInPorts.size());
    this.isTickScheduled = false;
    this.isExecuting = false;
  }

  public void requestScheduling(InPort<?> port, Object newMsg) {
    if (newMsg instanceof Message) {
      throw new IllegalArgumentException("Requested message object should be unwrapped and not instance of the rte class 'Message'");
    }
    requestScheduling(port);
  }

  public void requestSchedulingOfNewTick(InPort<?> port) {
    requestScheduling(port);
  }


  public void requestScheduling(InPort<?> port) {
    if (this.isExecuting) {
      return;  // After execution has finished, the scheduler will schedule the port by itself if there is a new message
    }

    if (isSyncComp) {
      requestSyncedScheduling();
    } else {
      requestTimedScheduling(port);
    }
  }

  protected void requestSyncedScheduling() {
    if (isTickScheduled) {
      return;
    } else if (allPortsHaveBufferedTick()) {
      for (InPort<?> p : allInPorts) {
        p.dropMessagesIgnoredBySync();  // Remove the processed tick
      }
      isTickScheduled = true;
    }
  }

  protected void requestTimedScheduling(InPort<?> port) {
    if (scheduledPorts.contains(port) || isTickScheduled || port.isBufferEmpty()) {
      return;
    }

    if (!port.isTickBlocked()) {
      scheduledPorts.add(port);
    } else if (scheduledPorts.isEmpty() && allPortsAreTickBlocked()) {
      isTickScheduled = true;
    }
  }

  public void executeNextSchedule() {
    if (isExecuting) {
      throw new IllegalStateException("Triggering the execution of a component that has not finished the already " +
        "running execution is not allowed.");
    }

    if (!scheduledPorts.isEmpty()) {
      executePortSchedule(scheduledPorts.getFirst());
    } else if (isTickScheduled) {
      executeTickSchedule();
    }
  }

  private void executePortSchedule(InPort<?> port) {
    if (!scheduledPorts.contains(port)) {
      throw new IllegalStateException("Can not execute unscheduled port.");
    }

    isExecuting = true;
    scheduledPorts.remove(port);
    component.handleMessage(port);
    port.pollBuffer();  // Remove processed message from port buffer
    isExecuting = false;

    if (!port.isBufferEmpty()) {
      this.requestScheduling(port);
    }
  }

  private void executeTickSchedule() {
    if (!isTickScheduled) {
      throw new IllegalStateException("Can not execute unscheduled tick.");
    }

    isExecuting = true;
    isTickScheduled = false;
    component.handleTick();
    // remove messages in front of tick and then the tick
    for (InPort<?> p : allInPorts) {
      while ((!p.isBufferEmpty()) && (p.peekBuffer() != Tick.get())) {
        p.pollBuffer();  // Remove all data messages
      }
      p.pollBuffer();  // Remove the processed tick
    }
    isExecuting = false;

    // Put ports that had messages buffered behind the tick into the scheduling queue again
    for (InPort<?> p : allInPorts) {
      if (!p.isBufferEmpty()) {
        this.requestScheduling(p);
      }
    }
  }

  void triggerComponentTickPort() {
    this.triggerComponentTickPort(1);
  }

  void triggerComponentTickPort(int ticks) {
    for (int i = 0; i < ticks; i++) {
      this.component.getTickPort().receive(Tick.get());
    }
  }

  public boolean isReadyToExecute() {
    return isTickScheduled || !scheduledPorts.isEmpty();
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
