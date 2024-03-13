<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("className")}
protected montiarc.rte.scheduling.ISchedule scheduler = new montiarc.rte.scheduling.FifoSchedule();

public montiarc.rte.scheduling.ISchedule getScheduler() {
  return this.scheduler;
}

public ${className} setScheduler(montiarc.rte.scheduling.ISchedule scheduler) {
  this.scheduler = scheduler;
  return this;
}