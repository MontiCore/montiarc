<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

protected void <@MethodNames.behaviorSetup/>() {
}

protected void <@MethodNames.handleSyncComputation/>() {
  if(getAllInPorts().stream()
    .filter(p -> p instanceof montiarc.rte.port.AbstractInPort)
    .map(p -> (montiarc.rte.port.AbstractInPort<?>) p)
    .anyMatch(montiarc.rte.port.AbstractInPort::isBufferEmpty)) {
  return;
  }

  if (<@MethodNames.inputsTickBlocked/>()) {
    <@MethodNames.dropTickOnAll/>();
    <@MethodNames.sendTickOnAll/>();
    <@MethodNames.handleSyncComputation/>();
    return;
  }

  getAllInPorts().stream()
    .filter(p -> p instanceof montiarc.rte.port.AbstractInPort)
    .map(p -> (montiarc.rte.port.AbstractInPort<?>) p)
    .filter(p -> !(((montiarc.rte.port.ITimeAwareInPort<?>) p).isTickBlocked()))
    .forEach(montiarc.rte.port.AbstractInPort::pollBuffer);
}
