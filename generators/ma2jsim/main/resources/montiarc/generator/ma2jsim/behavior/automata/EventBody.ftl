<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign compAutomatonClass>${ast.getName()}${suffixes.automaton()}<#if isTop>TOP</#if></#assign>
<#assign contextClass>${ast.getName()}${suffixes.context()}</#assign>
<#assign contextObj>${ast.getName()?uncap_first}${suffixes.context()}</#assign>

protected ${compAutomatonClass} (
  ${contextClass} ${contextObj},
  java.util.List${"<"}montiarc.rte.automaton.State${">"} states,
  montiarc.rte.automaton.State initial) {
    super(${contextObj}, states, initial);
}

<#assign automaton = helper.getAutomatonBehavior(ast).get()/>
<#assign isEvent = helper.isEventBased(automaton)/>
<#assign inTimed = helper.isComponentInputTimeAware(ast)/>
<#assign outTimed = helper.isComponentOutputTimeAware(ast)/>

<#-- Creating transition objects for tick-triggered transitions -->
<#assign transitionsForTickEvent = helper.getTransitionsForTickEvent(automaton)/>
<#list transitionsForTickEvent as transition>
  protected montiarc.rte.automaton.Transition ${prefixes.transition()}tick_${transition?counter} =
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [transition, []])};
</#list>

<#-- Generate method that executes tick-triggered transitions on tick events (if enabled). -->
public void tick() {
  <#list transitionsForTickEvent as tr>
      if(${prefixes.transition()}${prefixes.tick()}${tr?counter}.isEnabled(state)) {
        ${prefixes.transition()}${prefixes.tick()}${tr?counter}.execute(this);
      }<#sep> else </#sep>
  </#list>
}

<#-- Create transition objects for message-triggered transitions.
  -- Also create methods for the triggering input ports, executing these transitions.
  -->
<#list helper.getTransitionsForPortEvents(automaton) as portName, transitions>
  <#list transitions as transition>
    <#-- Transition objects -->
    protected montiarc.rte.automaton.Transition ${prefixes.transition()}${prefixes.message()}${portName}_${transition?counter} =
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [transition, [helper.getASTTransitionBody(transition).get().getSCEvent().getEventSymbol().getAdaptee()]])}; <#-- This long chain of calls assumes that this transition body has an event trigger and that it is an adapted port symbol - the helper method that created the map we're currently iterating over should assure that this is true -->
  </#list>

  <#-- Methods for the triggering input port, to execute matching transitions. -->
  public void ${prefixes.message()}${portName}() {
  <#list transitions as tr>
    if(${prefixes.transition()}${prefixes.message()}${portName}_${tr?counter}.isEnabled(state)) {
      ${prefixes.transition()}${prefixes.message()}${portName}_${tr?counter}.execute(this);
    }<#sep> else </#sep>
  </#list>
  <#if transitions?size != 0> else {</#if>
    context.${prefixes.port()}${portName}().pollBuffer();
  <#if transitions?size != 0> } </#if>
  }
</#list>

<#-- Methods for input ports that do not trigger any behavior. Such methods have not been create yet,
  -- but are a required part of the automaton API.
  -->
<#list helper.getInPortsNotTriggeringAnyTransition(automaton, ast) as port>
  <#assign handleMsgOnPort>${prefixes.message()}${port.getName()}</#assign>
  <#assign getPort>${prefixes.port()}${port.getName()}</#assign>

  public void ${handleMsgOnPort}() {
    context.${getPort}().pollBuffer();
  }
</#list>
