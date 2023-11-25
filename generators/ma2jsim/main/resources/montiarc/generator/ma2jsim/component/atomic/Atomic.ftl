<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#assign automaton = helper.getAutomatonBehavior(ast)/>
<#assign hasAutomaton = automaton.isPresent()/>
<#assign isEvent = hasAutomaton && helper.isEventBased(automaton.get())/>

<#list ast.getSymbol().getAllIncomingPorts() as portSym>
    ${tc.includeArgs("montiarc.generator.ma2jsim.component.atomic.HandleMessageOn.ftl", helper.asList(portSym, hasAutomaton, isEvent))}
</#list>

protected boolean <@MethodNames.inputsTickBlocked/>() {
  return this.getAllInPorts().stream().allMatch(montiarc.rte.port.ITimeAwareInPort::isTickBlocked);
}

protected void <@MethodNames.dropTickOnAll/>() {
  this.getAllInPorts().forEach(montiarc.rte.port.ITimeAwareInPort::dropBlockingTick);
}

protected void <@MethodNames.sendTickOnAll/>() {
  this.getAllOutPorts().forEach(montiarc.rte.port.AbstractOutPort::<@MethodNames.sendTick/>);
}

<#if hasAutomaton>
    ${tc.include("montiarc.generator.ma2jsim.behavior.automata.AutomatonReferencesInComponent.ftl")}
<#else>
    ${tc.include("montiarc.generator.ma2jsim.behavior.NoBehaviorReferencesInComponent.ftl")}
</#if>