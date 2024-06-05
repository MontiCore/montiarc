/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.ITimeAwareInPort;

import java.util.Collection;

public final class ModeComponentScheduler extends ComponentScheduler {
  private final CoordinatingScheduler coordinator;

  public ModeComponentScheduler(ITimedComponent component, Collection<ITimeAwareInPort<?>> inPorts, boolean isSync, CoordinatingScheduler coordinator) {
    super(component, inPorts, isSync);
    this.coordinator = coordinator;
  }


  @Override
  public void executeNextSchedule() {
    if (isReadyToExecute()) {
      super.executeNextSchedule();
    } else {
      throw new IllegalStateException("Dynamic component is not ready to execute. Maybe sub components have to be executed first.");
    }
  }

  @Override
  public boolean isReadyToExecute() {
    return super.isReadyToExecute() && !coordinator.isASubCompScheduled(this.component);
  }
}
