<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("className")}

protected java.util.Optional<montiarc.rte.scheduling.Scheduler> scheduler = java.util.Optional.empty();

public java.util.Optional<montiarc.rte.scheduling.Scheduler> getScheduler() {
  return this.scheduler;
}

public ${className} setScheduler(montiarc.rte.scheduling.Scheduler scheduler) {
  this.scheduler = java.util.Optional.of(scheduler);
  return this;
}

public ${className} setScheduler(java.util.Optional<montiarc.rte.scheduling.Scheduler> scheduler) {
  this.scheduler = scheduler;
  return this;
}
