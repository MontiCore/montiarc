<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("className")}

protected montiarc.rte.scheduling.TimeAwareScheduler scheduler = new montiarc.rte.scheduling.CoordinatingScheduler();

public montiarc.rte.scheduling.TimeAwareScheduler getScheduler() {
  return this.scheduler;
}

public ${className} setScheduler(montiarc.rte.scheduling.TimeAwareScheduler scheduler) {
  this.scheduler = scheduler;
  return this;
}