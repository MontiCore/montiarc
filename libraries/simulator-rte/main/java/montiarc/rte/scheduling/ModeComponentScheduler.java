/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.SimComponent;

public final class ModeComponentScheduler extends ComponentScheduler {

  private boolean hasReconfigured = true;

  public ModeComponentScheduler(SimComponent component,
                                CoordinatingScheduler coordinator) {
    super(component, coordinator);
  }

  @Override
  public void executeNextSchedule() {
    if (isReadyToExecute()) {
      if (scheduledMsgEventPorts.isEmpty() && component.isInitialized() && !canExecuteTick && !hasReconfigured) {
        executeTickReconfiguration();
      } else {
        super.executeNextSchedule();
      }
    } else {
      throw new IllegalStateException("Dynamic component is not ready to execute. Maybe sub components have to be executed first.");
    }
  }

  private void executeTickReconfiguration() {
    component.handleTickReconfiguration();
    hasReconfigured = true;
  }

  @Override
  public boolean isReadyToExecute() {
    return (super.isReadyToExecute() || !canExecuteTick && !hasReconfigured) && !coordinator.isASubCompScheduled(this.component);
  }

  @Override
  protected void coordinateNewTick() {
    super.coordinateNewTick();
    hasReconfigured = false;
  }
}
