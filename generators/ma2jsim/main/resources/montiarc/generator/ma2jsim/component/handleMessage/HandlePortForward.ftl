<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

protected void handlePortForward(montiarc.rte.port.TimeAwarePortForward<?> fwd) {
  if(!fwd.isBufferEmpty() && !fwd.isTickBlocked()) {
    fwd.forward();
    return;
  }
  if(<@MethodNames.inputsTickBlocked/>()) this.scheduleTick();
}