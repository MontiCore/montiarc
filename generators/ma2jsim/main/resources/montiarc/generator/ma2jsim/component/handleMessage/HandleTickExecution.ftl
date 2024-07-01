<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

<#assign modeAutomatonOpt = helper.getModeAutomaton(ast)>

protected void handleSyncedTickExecution() {

  <#if modeAutomatonOpt.isPresent()>
    // trigger mode automaton with tick
    getModeAutomaton().tick();
  </#if>
  // forward message to sub components / atomic behavior
  if (isAtomic) {
    if (<@MethodNames.getBehavior/>() != null) {
      <@MethodNames.getBehavior/>().tick();
    } else {
      // Remove message in front of tick
      getAllInPorts().stream()
        .filter(p -> !p.isTickBlocked())
        .forEach(montiarc.rte.port.IInPort::pollBuffer);
    }
    <@MethodNames.dropTickOnAll/>();
    <@MethodNames.sendTickOnAll/>();
  } else {
    for (montiarc.rte.port.ITimeAwareInPort<?> inP : getAllInPorts()) {
      // Forward the sync message (if existent) and forward the tick afterwards
      if (!inP.isTickBlocked()) {
        ((montiarc.rte.port.TimeAwarePortForComposition<?>) inP).forward();
      }
      ((montiarc.rte.port.TimeAwarePortForComposition<?>) inP).forward();
    }
    this.<@MethodNames.sendTickOnAllUnconnectedOutputs/>();
  }
}

protected void handleEventTickExecution() {
  ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

  <#if modeAutomatonOpt.isPresent()>
    getModeAutomaton().tick();
  </#if>

  // Forward tick to sub components / behavior;
  if (isAtomic) {
    if(<@MethodNames.getBehavior/>() != null) { <@MethodNames.getBehavior/>().tick(); }
    <@MethodNames.dropTickOnAll/>();
    <@MethodNames.sendTickOnAll/>();
  } else {
    getAllInPorts().stream()
      .map(montiarc.rte.port.TimeAwarePortForComposition.class::cast)
      .forEach(montiarc.rte.port.TimeAwarePortForComposition::forward);
    this.<@MethodNames.sendTickOnAllUnconnectedOutputs/>();
  }
}
