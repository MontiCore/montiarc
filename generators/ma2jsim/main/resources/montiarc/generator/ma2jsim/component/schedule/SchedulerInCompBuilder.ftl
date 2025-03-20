<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("className")}

protected montiarc.rte.scheduling.Scheduler scheduler = new montiarc.rte.scheduling.CoordinatingScheduler();

public montiarc.rte.scheduling.Scheduler getScheduler() {
  return this.scheduler;
}

public ${className} setScheduler(montiarc.rte.scheduling.Scheduler scheduler) {
  this.scheduler = scheduler;
  return this;
}