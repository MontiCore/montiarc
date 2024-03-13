<#-- (c) https://github.com/MontiCore/monticore -->
protected montiarc.rte.scheduling.ISchedule scheduler;

protected montiarc.rte.scheduling.ISchedule getScheduler() {
  return this.scheduler;
}

private void schedule(java.lang.Runnable action) {
  getScheduler().registerComputation(new montiarc.rte.scheduling.Computation(this, action));
}

private void scheduleTick() {
  getScheduler().registerComputation(new montiarc.rte.scheduling.TickComputation(this));
}
