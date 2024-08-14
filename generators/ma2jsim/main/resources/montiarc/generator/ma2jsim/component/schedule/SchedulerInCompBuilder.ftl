<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("className")}

protected montiarc.rte.scheduling.Scheduler scheduler = new montiarc.rte.scheduling.CoordinatingScheduler();

public montiarc.rte.scheduling.Scheduler getScheduler() {
  return this.scheduler;
}

public ${className} setScheduler(montiarc.rte.scheduling.Scheduler scheduler) {
  this.scheduler = scheduler;
  return this;
}