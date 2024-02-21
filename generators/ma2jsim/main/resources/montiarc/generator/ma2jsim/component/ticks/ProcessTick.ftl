<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#assign isAtomic = ast.getSymbol().isAtomic()/>
<#assign hasModeAutomaton = helper.getModeAutomaton(ast).isPresent()/>

protected void <@MethodNames.handleTick/>() {
  if(!<@MethodNames.inputsTickBlocked/>()) return;
  <#if isAtomic>
      if (!isSync) <@MethodNames.getBehavior/>().tick();
      <@MethodNames.dropTickOnAll/>();
      <@MethodNames.sendTickOnAll/>();
      if(!isSync || getAllInPorts().stream().allMatch(montiarc.rte.port.ITimeAwareInPort::hasBufferedTick)) {
        getAllInPorts().forEach(montiarc.rte.port.ITimeAwareInPort::continueAfterDroppedTick);
      }
  <#else>
      <#if hasModeAutomaton>
          <@MethodNames.getModeAutomaton/>().tick();
      </#if>
      getAllInPorts().stream()
        .filter(p -> p instanceof montiarc.rte.port.TimeAwarePortForward)
        .map(p -> (montiarc.rte.port.TimeAwarePortForward<?>) p)
        .forEach(montiarc.rte.port.TimeAwarePortForward::forward);
      <#list helper.getSubcomponentsWithoutInPorts(ast) as sub>
          ${prefixes.subcomp()}${sub.getName()}().<@MethodNames.receiveTick/>();
      </#list>
      getAllInPorts().stream()
        .filter(p -> p instanceof montiarc.rte.port.TimeAwarePortForward)
        .map(p -> (montiarc.rte.port.TimeAwarePortForward<?>) p)
        .forEach(fwd -> {
          while(!fwd.isBufferEmpty() && !fwd.isTickBlocked()) fwd.forward();
        });
  </#if>
}
