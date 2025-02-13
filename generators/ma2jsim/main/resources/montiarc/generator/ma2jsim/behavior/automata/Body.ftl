<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("automaton")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign compAutomatonClass>${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(ast.getSymbol())}<#if isTop>TOP</#if></#assign>
<#assign contextClass>${ast.getName()}${suffixes.context()}<@Util.printTypeParameters ast false/></#assign>
<#assign contextObj = ast.getName()?uncap_first + suffixes.context()/>
<#assign syncMsgType>${ast.getName()}${suffixes.syncMsg()}<@Util.printTypeParameters ast false/></#assign>
<#assign syncedPorts = helper.getSyncedInPortsOf(ast.getSymbol())/>
<#assign hasSyncedPorts = syncedPorts?size gt 0/>
<#assign tickMsgType = hasSyncedPorts?then(syncMsgType, "montiarc.rte.automaton.NoInput")/>



<#if hasSyncedPorts>
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/MsgGuardInterface.ftl", [])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/MsgActionInterface.ftl", [])}
</#if>

protected ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}<@Util.printTypeParameters ast false/> states;

<#-- Declaring transition fields for tick-triggered transitions -->
<#assign transitionsForTickEvent = helper.getTransitionsForTickEvent(automaton)/>
<#list transitionsForTickEvent as transition>
  protected montiarc.rte.automaton.Transition<${tickMsgType}> ${prefixes.transition()}tick_${transition?counter};
</#list>

protected ${compAutomatonClass} (
  ${contextClass} ${contextObj},
  ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}<@Util.printTypeParameters ast false/> states,
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

  <#-- Create transition objects for message-triggered transitions. -->
  <#list helper.getTransitionsForPortEvents(ast, automaton) as port, transitions>
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
public void tick(${syncMsgType} syncedInputs) {
  <#assign transitionArg =  hasSyncedPorts?then("syncedInputs", "null")/>
  <#list transitionsForTickEvent as tr>
    <#assign transition_field = prefixes.transition() + prefixes.tick() + tr?counter>
    if(${transition_field}.isEnabled(state, ${transitionArg})) {
      ${transition_field}.execute(this, ${transitionArg});
    }<#sep> else </#sep>
  </#list>

  this.getState().doActionWithSuper();
}

<#-- Declare transition objects for message-triggered transitions.
  -- Also create methods for the triggering input ports, executing these transitions.
  -->
<#list helper.getTransitionsForPortEvents(ast, automaton) as port, transitions>
  <#assign portName = port.getName()>
  <#list transitions as transition>
    <#-- Transition objects -->
    protected montiarc.rte.automaton.Transition<<@Util.getTypeString port.getType() true/>> ${prefixes.transition()}${prefixes.message()}${portName}_${transition?counter};
  </#list>

  <#-- Methods for the triggering input port, to execute matching transitions. -->
  @Override
  public void ${prefixes.message()}${portName}${helper.portVariantSuffix(ast, port)}(<@Util.getTypeString port.getType()/> msg) {
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
  <#assign handleMsgOnPort>${prefixes.message()}${port.getName()}${helper.portVariantSuffix(ast, port)}</#assign>

  @Override
  public void ${handleMsgOnPort}(<@Util.getTypeString port.getType()/> msg) {
    <#if helper.isSync(port)>
      de.se_rwth.commons.logging.Log.warn("Event behavior method was illegally called for synchronous port '${port.getName()}'.");
    </#if>
  }
</#list>

<#-- Methods for ports from other variants of the same component -->
<#list helper.getInPortsWithSuffixesOfOtherVariants(ast.getSymbol()) as port, varSuffix>
  <#assign handleMsgOnPort>${prefixes.message()}${port.getName()}${varSuffix}</#assign>

  @Override
  public void ${handleMsgOnPort}(<@Util.getTypeString port.getType()/> msg) {
    throw new IllegalStateException("This event method is not available in this variant (${port.getName()}${varSuffix}).");
  }
</#list>
