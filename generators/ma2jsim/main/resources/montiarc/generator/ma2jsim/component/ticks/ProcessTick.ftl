<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#assign isAtomic = ast.getSymbol().isAtomic()/>
<#assign isEventAutomaton = helper.getAutomatonBehavior(ast).isPresent()
                         && helper.isEventBased(helper.getAutomatonBehavior(ast).get())/>
<#assign hasModeAutomaton = helper.getModeAutomaton(ast).isPresent()/>

protected void <@MethodNames.handleTick/>() {
  if(!<@MethodNames.inputsTickBlocked/>()) return;
  <#if isAtomic>
      <#if isEventAutomaton>
          <@MethodNames.getBehavior/>().tick();
      </#if>
      <@MethodNames.dropTickOnAll/>();
      <@MethodNames.sendTickOnAll/>();
      <#if !isEventAutomaton>if(getAllInPorts().stream().allMatch(montiarc.rte.port.ITimeAwareInPort::hasBufferedTick))</#if>
      getAllInPorts().forEach(montiarc.rte.port.ITimeAwareInPort::continueAfterDroppedTick);
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
