<#-- (c) https://github.com/MontiCore/monticore -->
protected montiarc.rte.scheduling.TimeAwareScheduler scheduler;

protected montiarc.rte.scheduling.TimeAwareScheduler getScheduler() {
  return this.scheduler;
}

public void run() {
  this.scheduler.run(this);
}

public void run(int ticks) {
  this.scheduler.run(this, ticks);
}

public void unregisterFromScheduler() {
  this.scheduler.unregister(this);
}
