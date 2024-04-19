<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

<#assign modeAutomatonOpt = helper.getModeAutomaton(ast)>

protected void handleSyncedTickExecution() {

  // for all incoming ports p: discard any message before the tick-preceeding one (if existent)
  getAllInPorts().stream()
    .map(montiarc.rte.port.SyncAwareInPort.class::cast)
    .forEach(montiarc.rte.port.SyncAwareInPort::dropMessagesIgnoredBySync);

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
  }

  // Replay queued messages behind tick
  getAllInPorts().forEach(montiarc.rte.port.ITimeAwareInPort::continueAfterDroppedTick);

  // If there are no incoming ports, schedule the next tick
  // (because behavior will not be triggered externally)
  if (this.getAllInPorts().isEmpty()) { this.scheduleTick(); }
}

protected void handleEventTickExecution() {
  ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

  // for all incoming ports p and messages on them until tick:
    // trigger mode automaton with m on p;
    // forward m to sub components / behavior;
  <#list ast.getSymbol().getAllIncomingPorts() as inPort>
    <#assign existenceConditions = helper.getExistenceCondition(ast, inPort)/>
    <#assign portGetterName>${prefixes.port()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}</#assign>

    if(<#if existenceConditions?has_content>${prettyPrinter.prettyprint(existenceConditions)}<#else>true</#if>) {
      while (!${portGetterName}().isBufferEmpty() && !${portGetterName}().isTickBlocked()) {
        // Trigger mode automaton (if existent) with the message on the port and afterwards forward it to the sub components / behavior
        <#if modeAutomatonOpt.isPresent() && helper.isEventBased(modeAutomatonOpt.get())>
          getModeAutomaton().${prefixes.message()}${inPort.getName()}();
        </#if>
        if (isAtomic) {
          if (<@MethodNames.getBehavior/>() != null) {
            ((${ast.getName()}${suffixes.events()}) <@MethodNames.getBehavior/>()).${prefixes.message()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}();
          } else { ${portGetterName}().pollBuffer(); }
        } else {
          ((montiarc.rte.port.TimeAwarePortForComposition<?>) ${portGetterName}()).forward();
        }
      }
    }
  </#list>

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
  }

  // Replay queued messages behind tick
  getAllInPorts().forEach(montiarc.rte.port.ITimeAwareInPort::continueAfterDroppedTick);

  // If there are no incoming ports, schedule the next tick
  // (because behavior will not be triggered externally)
  if (this.getAllInPorts().isEmpty()) { this.scheduleTick(); }
}
