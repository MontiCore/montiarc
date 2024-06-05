/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.rte.port.IInPort;
import montiarc.rte.port.ITimeAwareInPort;
import montiarc.rte.port.SyncAwareInPort;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Set;

public class ComponentScheduler {

  protected final ITimedComponent component;
  protected final Set<IInPort<?>> allInPorts;
  @Deprecated protected final boolean isSyncComp;

  protected final Deque<ITimeAwareInPort<?>> scheduledPorts;
  protected boolean isTickScheduled;


  public ComponentScheduler(ITimedComponent component, Collection<ITimeAwareInPort<?>> inPorts, boolean isSync) {
    this.component = component;
    this.allInPorts = Set.copyOf(inPorts);
    this.isSyncComp = isSync;

    this.scheduledPorts = new ArrayDeque<>(this.allInPorts.size());
    this.isTickScheduled = false;

  }

  public void requestScheduling(ITimeAwareInPort<?> port, Object newMsg) {
    if (newMsg instanceof Message) {
      throw new IllegalArgumentException("Requested message object should be unwrapped and not instance of the rte class 'Message'");
    }
    requestScheduling(port);
  }

  public void requestSchedulingOfNewTick(ITimeAwareInPort<?> port) {
    requestScheduling(port);
  }


  public void requestScheduling(ITimeAwareInPort<?> port) {
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
      allInPorts.stream().map(SyncAwareInPort.class::cast)
        .forEach(SyncAwareInPort::dropMessagesIgnoredBySync);
      isTickScheduled = true;
    }
  }

  protected void requestTimedScheduling(ITimeAwareInPort<?> port) {
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
    if (!scheduledPorts.isEmpty()) {
      executePortSchedule(scheduledPorts.getFirst());
    } else if (isTickScheduled) {
      executeTickSchedule();
    }
  }

  private void executePortSchedule(ITimeAwareInPort<?> port) {
    if (!scheduledPorts.contains(port)) {
      throw new IllegalStateException("Can not execute unscheduled port.");
    }

    scheduledPorts.remove(port);
    component.handleMessage(port);

    if (!port.isBufferEmpty()) {
      this.requestScheduling(port);
    }
  }

  private void executeTickSchedule() {
    if (!isTickScheduled) {
      throw new IllegalStateException("Can not execute unscheduled tick.");
    }

    isTickScheduled = false;
    component.handleTick();

    allInPorts.stream()
      .filter(p -> !p.isBufferEmpty())
      .map(ITimeAwareInPort.class::cast)
      .forEach(this::requestScheduling);
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

  public void runIfPossible(int ticks) {
    if (ticks < 0) {
      this.run();
    }

    for (int i = 0; i < ticks; i++) {
      if (!isReadyToExecute()) {
        break;
      }
      executeNextSchedule();
    }
  }

  private boolean allPortsHaveBufferedTick() {
    return allInPorts.stream().allMatch(IInPort::hasBufferedTick);
  }

  private boolean allPortsAreTickBlocked() {
    return allInPorts.stream().allMatch(p -> ((ITimeAwareInPort<?>) p).isTickBlocked());
  }
}
