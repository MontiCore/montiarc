<#-- (c) https://github.com/MontiCore/monticore -->
protected montiarc.rte.scheduling.ISchedule scheduler = new montiarc.rte.scheduling.InstantSchedule();

protected montiarc.rte.scheduling.ISchedule getScheduler() {
  return this.scheduler;
}

protected void setScheduler(montiarc.rte.scheduling.ISchedule scheduler) {
  this.scheduler = scheduler;
}

private void schedule(java.lang.Runnable action) {
  getScheduler().registerComputation(new montiarc.rte.scheduling.Computation(this, action));
}
