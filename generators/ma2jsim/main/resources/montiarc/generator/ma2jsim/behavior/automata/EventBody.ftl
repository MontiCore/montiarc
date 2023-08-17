<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

protected ${ast.getName()}${suffixes.automaton()}<#if isTop>TOP</#if> (
  ${ast.getName()}${suffixes.context()} ${ast.getName()?uncap_first}${suffixes.context()},
  java.util.List${"<"}montiarc.rte.automaton.State${">"} states,
  montiarc.rte.automaton.State initial) {
    super(${ast.getName()?uncap_first}${suffixes.context()}, states, initial);
}

<#assign automaton = helper.getAutomatonBehavior(ast).get()/>
<#assign isEvent = helper.isEventBased(automaton)/>
<#assign inTimed = helper.isComponentInputTimeAware(ast)/>
<#assign outTimed = helper.isComponentOutputTimeAware(ast)/>

<#assign transitionsWithoutEvent = helper.getTransitionsWithoutEvent(automaton)/>
<#list transitionsWithoutEvent as transition>
  protected montiarc.rte.automaton.Transition ${prefixes.transition()}${prefixes.noStimulus()}${transition?counter} =

  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [transition, []])};
</#list>

<#assign transitionsForTickEvent = helper.getTransitionsForTickEvent(automaton)/>
<#list transitionsForTickEvent as transition>
  protected montiarc.rte.automaton.Transition ${prefixes.transition()}tick_${transition?counter} =
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [transition, []])};
</#list>

public void tick() {
  <#list transitionsForTickEvent as tr>
      if(${prefixes.transition()}${prefixes.tick()}${tr?counter}.isEnabled(state)) {
        ${prefixes.transition()}${prefixes.tick()}${tr?counter}.execute(this);
      }<#sep> else </#sep>
  </#list>
  <#if (transitionsForTickEvent?size > 0) && (transitionsWithoutEvent?size > 0)> else </#if>
  <#list transitionsWithoutEvent as tr>
    if(${prefixes.transition()}${prefixes.noStimulus()}${tr?counter}.isEnabled(state)) {
      ${prefixes.transition()}${prefixes.noStimulus()}${tr?counter}.execute(this);
    }<#sep> else </#sep>
  </#list>
}

<#list helper.getTransitionsForPortEvents(automaton) as portName, transitions>
  <#list transitions as transition>
    protected montiarc.rte.automaton.Transition ${prefixes.transition()}${prefixes.message()}${portName}_${transition?counter} =
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [transition, [helper.getASTTransitionBody(transition).get().getSCEvent().getEventSymbol().getAdaptee()]])}; <#-- This long chain of calls assumes that this transition body has an event trigger and that it is an adapted port symbol - the helper method that created the map we're currently iterating over should assure that this is true -->
  </#list>

  public void ${prefixes.message()}${portName}() {
  <#list transitions as tr>
    if(${prefixes.transition()}${prefixes.message()}${portName}_${tr?counter}.isEnabled(state)) {
      ${prefixes.transition()}${prefixes.message()}${portName}_${tr?counter}.execute(this);
    }<#sep> else </#sep>
  </#list>
  <#if (transitions?size > 0) && (transitionsWithoutEvent?size > 0)> else </#if>
  <#list transitionsWithoutEvent as tr>
    if(${prefixes.transition()}${prefixes.noStimulus()}${tr?counter}.isEnabled(state)) {
    ${prefixes.transition()}${prefixes.noStimulus()}${tr?counter}.execute(this);
    }<#sep> else </#sep>
  </#list>
  }
</#list>