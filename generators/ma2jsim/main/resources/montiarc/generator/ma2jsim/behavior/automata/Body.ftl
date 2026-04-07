<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("automaton")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign compAutomatonClass>${ast.getName()}${suffixes.automaton()}${helper.getVariantHelper().variantSuffix(ast.getSymbol())}<#if isTop>TOP</#if></#assign>
<#assign contextClass>${ast.getName()}${suffixes.context()}<@Util.printTypeParameters ast false/></#assign>
<#assign contextObj = ast.getName()?uncap_first + suffixes.context()/>
<#assign syncMsgType>${ast.getName()}${suffixes.syncMsg()}<@Util.printTypeParameters ast false/></#assign>
<#assign syncedPorts = helper.getComponentHelper().getSyncedInPortsOf(ast.getSymbol())/>
<#assign hasSyncedPorts = syncedPorts?size gt 0/>
<#assign tickMsgType = hasSyncedPorts?then(syncMsgType, "montiarc.rte.automaton.NoInput")/>
<#assign portList = ast.getSymbol().getAllIncomingPorts()/>


<#if hasSyncedPorts>
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/MsgGuardInterface.ftl", [])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/MsgActionInterface.ftl", [])}
</#if>

protected ${ast.getName()}${suffixes.states()}${helper.getVariantHelper().variantSuffix(ast.getSymbol())}<@Util.printTypeParameters ast false/> states;

<#-- Declaring transition fields for tick-triggered transitions -->
<#assign transitionsForTickEvent = helper.getBehaviorHelper().getTransitionsWithoutEvent(automaton)/>
<#list transitionsForTickEvent as transition>
  protected montiarc.rte.automaton.Transition<${tickMsgType}> ${prefixes.transition()}tick_${transition?counter};
</#list>

<#assign innerTransitionsForTickEvent = helper.getBehaviorHelper().getInnerTransitionsWithoutTrigger(automaton)/>
<#list innerTransitionsForTickEvent as transition>
  protected montiarc.rte.automaton.Transition<${tickMsgType}> ${prefixes.innerTransition()}tick_${transition?counter};
</#list>

protected ${compAutomatonClass} (
  ${contextClass} ${contextObj},
  ${ast.getName()}${suffixes.states()}${helper.getVariantHelper().variantSuffix(ast.getSymbol())}<@Util.printTypeParameters ast false/> states,
  montiarc.rte.automaton.State initial, String name) {
    super(${contextObj}, initial, name);
    this.states = states;
  <#-- Create transitions on tick events (if enabled). -->
  <#list transitionsForTickEvent as transition>
    ${prefixes.transition()}tick_${transition?counter} =
    <#if syncedPorts?size == 0>
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [automaton, transition, true, false, []])};
    <#else>
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [automaton, transition, false, true, syncedPorts])};
    </#if>
  </#list>

  <#-- Create inner transitions objects on tick events -->
  <#list innerTransitionsForTickEvent as transition>
    ${prefixes.innerTransition()}tick_${transition?counter} =
    <#if syncedPorts?size == 0>
       ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [automaton, transition, true, false, []])};
    <#else>
       ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [automaton, transition, false, true, syncedPorts])};
    </#if>
  </#list>

  <#-- Create transition objects for message-triggered transitions. -->
  <#list portList as port>
    <#assign portName = port.getName()/>

    <#assign msgTriggeredTransitions = helper.getBehaviorHelper().getAllTransitionsForPortWithEventTrigger(ast, automaton, port)/>
    <#list msgTriggeredTransitions as transition>
      <#-- Transition objects -->
      ${prefixes.transition()}${prefixes.message()}${portName}_${transition?counter} =
        ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [automaton, transition, false, false, [port]])};
    </#list>

    <#assign msgTriggeredInnerTransitions = helper.getBehaviorHelper().getAllInnerTransitionsForPortWithEventTrigger(ast, automaton, port)/>
    <#list msgTriggeredInnerTransitions as innerTransition>
        <#-- InnerTransition objects -->
        ${prefixes.innerTransition()}${prefixes.message()}${portName}_${innerTransition?counter} =
          ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [automaton, innerTransition, false, false, [port]])};
    </#list>
  </#list>

}

<#-- Generate method that executes tick-triggered (inner) transitions on tick events (if enabled). -->
@Override
public void tick(${syncMsgType} syncedInputs) {
  <#assign transitionArg =  hasSyncedPorts?then("syncedInputs", "null")/>
  <#assign transitionsSize = transitionsForTickEvent?size + innerTransitionsForTickEvent?size/>
  java.util.Map<Integer, montiarc.rte.automaton.Transition<${tickMsgType}>> enabledTransitions = new java.util.LinkedHashMap<>(${transitionsSize});

  <#list transitionsForTickEvent as tr>
    <#assign transition_field = prefixes.transition() + prefixes.tick() + tr?counter/>
    if(${transition_field}.isEnabled(state, ${transitionArg})) {
       enabledTransitions.put(enabledTransitions.size(), ${transition_field});
    }
  </#list>

  <#list innerTransitionsForTickEvent as tr>
     <#assign transition_field = prefixes.innerTransition() + prefixes.tick() + tr?counter/>
     if(${transition_field}.isEnabled(state, ${transitionArg})) {
        enabledTransitions.put(enabledTransitions.size(), ${transition_field});
     }
  </#list>

  if (!enabledTransitions.isEmpty()) {
    getContext().getOracle()
      .decideAmong(enabledTransitions)
      .execute(this, ${transitionArg});
  }

  this.getState().doActionWithSuper();
}

<#-- Declare transition objects for message-triggered transitions.
  -- Also create methods for the triggering input ports, executing these transitions.
  -->
<#list portList as port>
  <#assign portName = port.getName()/>
  <#assign handleMsgOnPort>${prefixes.message()}${port.getName()}${helper.getVariantHelper().portVariantSuffix(ast, port)}</#assign>

  <#assign msgTriggeredTransitions = helper.getBehaviorHelper().getAllTransitionsForPortWithEventTrigger(ast, automaton, port)/>
  <#list msgTriggeredTransitions as transition>
      <#-- Transition objects -->
      protected montiarc.rte.automaton.Transition<<@Util.getTypeString port.getType() true/>> ${prefixes.transition()}${prefixes.message()}${portName}_${transition?counter};
  </#list>

  <#assign msgTriggeredInnerTransitions = helper.getBehaviorHelper().getAllInnerTransitionsForPortWithEventTrigger(ast, automaton, port)/>
  <#list msgTriggeredInnerTransitions as innerTransition>
      <#-- InnerTransition objects -->
      protected montiarc.rte.automaton.Transition<<@Util.getTypeString port.getType() true/>> ${prefixes.innerTransition()}${prefixes.message()}${portName}_${innerTransition?counter};
  </#list>

  <#assign transitionsSize = msgTriggeredTransitions?size + msgTriggeredInnerTransitions?size/>
  <#if transitionsSize gt 0>
    <#-- Methods for the triggering input port, to execute matching transitions. -->
    @Override
    public void ${handleMsgOnPort}(<@Util.getTypeString port.getType()/> msg) {
      java.util.Map<Integer, montiarc.rte.automaton.Transition<<@Util.getTypeString port.getType() true/>>> enabledTransitions = new java.util.LinkedHashMap<>(${transitionsSize});

      <#list msgTriggeredTransitions as tr>
        <#assign transition_field = prefixes.transition() + prefixes.message() + portName + "_" + tr?counter/>
        if(${transition_field}.isEnabled(state, msg)) {
          enabledTransitions.put(enabledTransitions.size(), ${transition_field});
        }
      </#list>

      <#list msgTriggeredInnerTransitions as tr>
        <#assign transition_field = prefixes.innerTransition() + prefixes.message() + portName + "_" + tr?counter/>
        if(${transition_field}.isEnabled(state, msg)) {
          enabledTransitions.put(enabledTransitions.size(), ${transition_field});
        }
      </#list>

      if (!enabledTransitions.isEmpty()) {
        getContext().getOracle()
          .decideAmong(enabledTransitions)
          .execute(this, msg);
      }
    }
  <#else>
    <#-- Methods for input ports that do not trigger any behavior. Such methods have not been create yet,
    -- but are a required part of the automaton API.
    -->
    @Override
    public void ${handleMsgOnPort}(<@Util.getTypeString port.getType()/> msg) {
      <#if helper.getComponentHelper().isSync(port)>
        de.se_rwth.commons.logging.Log.warn("Event behavior method was illegally called for synchronous port '${port.getName()}'.");
      </#if>
    }
  </#if>
</#list>

<#-- Methods for ports from other variants of the same component -->
<#list helper.getVariantHelper().getInPortsWithSuffixesOfOtherVariants(ast.getSymbol()) as port, varSuffix>
  <#assign handleMsgOnPort>${prefixes.message()}${port.getName()}${varSuffix}</#assign>

  @Override
  public void ${handleMsgOnPort}(<@Util.getTypeString port.getType()/> msg) {
    throw new IllegalStateException("This event method is not available in this variant (${port.getName()}${varSuffix}).");
  }
</#list>
