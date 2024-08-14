<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop", "automaton")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign compAutomatonClass>${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(ast.getSymbol())}<#if isTop>TOP</#if></#assign>
<#assign contextClass>${ast.getName()}${suffixes.context()}<@Util.printTypeParameters ast false/></#assign>
<#assign contextObj>${ast.getName()?uncap_first}${suffixes.context()}</#assign>
<#assign syncMsgClass>${ast.getName()}${suffixes.syncMsg()}</#assign>

protected ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}<@Util.printTypeParameters ast false/> states;

<#-- Creating transition objects for tick-triggered transitions -->
<#assign transitionsForTickEvent = helper.getTransitionsForTickEvent(automaton)/>
<#list transitionsForTickEvent as transition>
protected montiarc.rte.automaton.Transition<montiarc.rte.automaton.NoInput> ${prefixes.transition()}tick_${transition?counter};
</#list>

protected ${compAutomatonClass} (
  ${contextClass} ${contextObj},
  ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}<@Util.printTypeParameters ast false/> states,
  montiarc.rte.automaton.State initial) {
    super(${contextObj}, initial);
    this.states = states;
  <#-- Create transitions on tick events (if enabled). -->
  <#list transitionsForTickEvent as transition>
    ${prefixes.transition()}tick_${transition?counter} =
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [automaton, transition, true, false, []])};
  </#list>
  <#-- Create transition objects for message-triggered transitions. -->
  <#list helper.getTransitionsForPortEvents(automaton) as port, transitions>
    <#assign portName = port.getName()>
    <#list transitions as transition>
    <#-- Transition objects -->
    ${prefixes.transition()}${prefixes.message()}${portName}_${transition?counter} =
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [automaton, transition, false, false, [port]])};
    </#list>
  </#list>
}

<#-- Generate method that executes tick-triggered transitions on tick events (if enabled). -->
@Override
public void tick(${syncMsgClass} nullMsg) {
  <#list transitionsForTickEvent as tr>
      if(${prefixes.transition()}${prefixes.tick()}${tr?counter}.isEnabled(state, null)) {
        ${prefixes.transition()}${prefixes.tick()}${tr?counter}.execute(this, null);
      }<#sep> else </#sep>
  </#list>
}

<#-- Create transition objects for message-triggered transitions.
  -- Also create methods for the triggering input ports, executing these transitions.
  -->
<#list helper.getTransitionsForPortEvents(automaton) as port, transitions>
  <#assign portName = port.getName()>
  <#list transitions as transition>
    <#-- Transition objects -->
    protected montiarc.rte.automaton.Transition<<@Util.getTypeString port.getType() true/>> ${prefixes.transition()}${prefixes.message()}${portName}_${transition?counter};
  </#list>

  <#-- Methods for the triggering input port, to execute matching transitions. -->
  @Override
  public void ${prefixes.message()}${portName}(<@Util.getTypeString port.getType()/> msg) {
  <#list transitions as tr>
    if(${prefixes.transition()}${prefixes.message()}${portName}_${tr?counter}.isEnabled(state, msg)) {
      ${prefixes.transition()}${prefixes.message()}${portName}_${tr?counter}.execute(this, msg);
    }<#sep> else </#sep>
  </#list>
  }
</#list>

<#-- Methods for input ports that do not trigger any behavior. Such methods have not been create yet,
  -- but are a required part of the automaton API.
  -->
<#list helper.getInPortsNotTriggeringAnyTransition(automaton, ast) as port>
  <#assign handleMsgOnPort>${prefixes.message()}${port.getName()}</#assign>
  <#assign getPort>${prefixes.port()}${port.getName()}</#assign>

  @Override
  public void ${handleMsgOnPort}(<@Util.getTypeString port.getType()/> msg) {}
</#list>
