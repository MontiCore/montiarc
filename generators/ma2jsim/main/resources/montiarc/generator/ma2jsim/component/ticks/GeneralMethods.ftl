<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

protected boolean <@MethodNames.inputsTickBlocked/>() {
  return this.getAllInPorts().stream().allMatch(montiarc.rte.port.ITimeAwareInPort::isTickBlocked);
}

protected boolean <@MethodNames.inputsTickBuffered/>() {
  return this.getAllInPorts().stream().allMatch(montiarc.rte.port.IInPort::hasBufferedTick);
}

protected void <@MethodNames.dropTickOnAll/>() {
  this.getAllInPorts().forEach(montiarc.rte.port.ITimeAwareInPort::dropBlockingTick);
}

protected void <@MethodNames.sendTickOnAll/>() {
  this.getAllOutPorts().forEach(montiarc.rte.port.AbstractOutPort::sendTick);
}
