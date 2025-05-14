/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.SimComponent;

public final class ModeComponentScheduler extends ComponentScheduler {
  private final CoordinatingScheduler coordinator;

  public ModeComponentScheduler(SimComponent component,
                                CoordinatingScheduler coordinator) {
    super(component);
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
