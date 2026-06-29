<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#-- Signature explanation:
  - noInputsForActions: Whether the built transition does not contain a message information.
  -                     E.g., because it is a event-trick-triggered transition.
  - useSyncMsg: Whether the transition is sync-tick-triggered and thereby uses the SyncMessage class
  - inPorts: If this transition builder should build an event transition with a message, then supply the port symbol to
  -          which the message belongs. If this transition builder should build a synced-tick-transition, then supply
  -          all incoming ports of the component.
  -
  - Note that it is illegal to supply "false" as argument to both "noInputsForActions" and "useSyncMsg"
  -->
${tc.signature("automaton", "transition", "noInputsForActions", "useSyncMsg", "inPorts")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign isInnerTransition = helper.getBehaviorHelper().isInnerTransition(transition)/>
<#assign body = helper.getBehaviorHelper().getASTTransitionBody(transition)/>

<#assign lambdaArgs>
  <#if noInputsForActions>
  <#-- "in" is a reserved keywoard and by that not usable as name, e.g., for fields.
    -- by using it as the name for the lambda parameter, we thus we avoid name collisions
    -->
    in
  <#else><#list inPorts as inPort>${inPort.getName()}<#sep>, </#list> </#if>
</#assign>

<#assign transitionMsgType>
  <#if noInputsForActions> montiarc.rte.automaton.NoInput
  <#elseif useSyncMsg>${ast.getName()}${suffixes.syncMsg()}<@Util.printTypeParameters ast false/>
  <#else><#assign port = inPorts[0]> <@Util.getTypeString port.getType() true/></#if>
</#assign>

<#assign sourceState>
  <#if !isInnerTransition>
    this.states.${prefixes.state()}${transition.getSource().getName()}
  <#else>
    this.states.${prefixes.state()}${helper.getBehaviorHelper().getInnerTransitionState(automaton, transition).getName()}
  </#if>
</#assign>
<#assign targetState>
  <#if !isInnerTransition>
    this.states.${prefixes.state()}${transition.getTarget().getName()}
  <#else>
    this.states.${prefixes.state()}${helper.getBehaviorHelper().getInnerTransitionState(automaton, transition).getName()}
  </#if>
</#assign>

new montiarc.rte.automaton.TransitionBuilder<${transitionMsgType}>()
  .setSource(${sourceState})
  .setTarget(${targetState})
  .setGuard(<@guard/>)
  .setAction(<@action/>)
  .build()

<#macro guard>
  <@guardCast/> (${lambdaArgs}) ->
  <#if body.isPresent() && body.get().isPresentPre()>
    {
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getComponentHelper().getFeatures(ast)])}
      return ${javaPrinter.generateCode(body.get().getPre())};
    }
  <#else>
    true
  </#if>
</#macro>

<#macro action>
  <@actionCast/> (${lambdaArgs}) -> {

  <#-- Calculate whether the current state is in a state hierarchy -->


  <#if !isInnerTransition>
    <@exitAction/>
  <#else>
    <#assign hasSubstates = helper.getBehaviorHelper().getSubstates(helper.getBehaviorHelper().getInnerTransitionState(automaton, transition)) ? has_content>
    <#if hasSubstates>
      ${sourceState}.exitSubSkipSelf(state);
    </#if>
  </#if>

  <#-- 2. Actual transition action -->
  <#if body.isPresent() && body.get().isPresentTransitionAction()>
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getComponentHelper().getFeatures(ast)])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
    ${javaPrinter.generateCode(body.get().getTransitionAction())}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}
  </#if>

  <#if !isInnerTransition>
    <@entryAction/>
  <#else>
    <#assign hasSubstates = helper.getBehaviorHelper().getSubstates(helper.getBehaviorHelper().getInnerTransitionState(automaton, transition)) ? has_content>
    <#if hasSubstates>
      <#assign stateToEnter = helper.getBehaviorHelper().getInitialSubstates(helper.getBehaviorHelper().getInnerTransitionState(automaton, transition))?first<#-- 1. initial state -->>
    this.states.${prefixes.state()}${stateToEnter.getName()}.enterWithSub();
    </#if>
  </#if>
}
</#macro>

<#macro exitAction>
  <#assign source = transition.getSource().getNameDefinition()>
  <#assign target = transition.getTarget().getNameDefinition()>
  <#-- Calculate whether there the current state is in a state hierarchy that also contains the target state -->
  <#assign commonSuperstate = automaton.findCommonSuperstate(source, target)!>
  <#assign haveCommonSuperstate = commonSuperstate != "">

  <#-- 1. Execute exit actions -->
  <#if !haveCommonSuperstate>
  <#-- The root in the source state's hierarchy -->
    <#assign stateToExit = automaton.getAncestors(source)?last>
  <#elseif commonSuperstate == source || commonSuperstate == target>
  <#-- Transition sources and targets must always be left -->
    <#assign stateToExit = commonSuperstate>
  <#else>
  <#-- Source and target have a common ancestor (that is not the source),
    -- so all relatives between source and the common ancestor (excluding it) must be left.
    -- -> select the relative which is a direct child of the common ancestor
    -->
    <#assign stateToExit = automaton.getAncestorsInbetween(source, commonSuperstate)?first<#-- 1. Child of common superstate-->>
  </#if>
    this.states.${prefixes.state()}${stateToExit.getName()}.exitSub(state);
</#macro>

<#macro entryAction>
  <#assign source = transition.getSource().getNameDefinition()>
  <#assign target = transition.getTarget().getNameDefinition()>
  <#-- Calculate whether there the current state is in a state hierarchy that also contains the target state -->
  <#assign commonSuperstate = automaton.findCommonSuperstate(source, target)!>
  <#assign haveCommonSuperstate = commonSuperstate != "">

  <#-- 3. Enter actions -->
  <#if haveCommonSuperstate
  && commonSuperstate == source
  && commonSuperstate != target  <#-- On the target we always call .enterWithSub(), also if it is the superstate. -->
  >
      this.states.${prefixes.state()}${commonSuperstate.getName()}.enter();
  </#if>

  <#assign statesToEnter = haveCommonSuperstate?then(
  automaton.getAncestorsInbetween(target, commonSuperstate),
  automaton.getAncestors(target)?reverse
  )>
  <#list statesToEnter as state>
    <#if state?is_last>
      this.states.${prefixes.state()}${state.getName()}.enterWithSub();
    <#else>
      this.states.${prefixes.state()}${state.getName()}.enter();
    </#if>
  </#list>
</#macro>

<#macro guardCast>
  <#if useSyncMsg>(${ast.getName()}${suffixes.msgGuard()}<@Util.printTypeParameters ast false/>)
  <#elseif !noInputsForActions>
    <#assign port = inPorts[0]>
    <#assign type = port.getType()>
    <#if helper.getTypeHelper().isUnboxedChar(type)>(montiarc.rte.automaton.guards.CharGuard)
    <#elseif helper.getTypeHelper().isUnboxedBoolean(type)>(montiarc.rte.automaton.guards.BooleanGuard)
    <#elseif helper.getTypeHelper().isUnboxedByte(type)>(montiarc.rte.automaton.guards.ByteGuard)
    <#elseif helper.getTypeHelper().isUnboxedShort(type)>(montiarc.rte.automaton.guards.ShortGuard)
    <#elseif helper.getTypeHelper().isUnboxedInt(type)>(montiarc.rte.automaton.guards.IntGuard)
    <#elseif helper.getTypeHelper().isUnboxedLong(type)>(montiarc.rte.automaton.guards.LongGuard)
    <#elseif helper.getTypeHelper().isUnboxedFloat(type)>(montiarc.rte.automaton.guards.FloatGuard)
    <#elseif helper.getTypeHelper().isUnboxedDouble(type)>(montiarc.rte.automaton.guards.DoubleGuard)</#if>
  </#if>
</#macro>

<#macro actionCast>
  <#if useSyncMsg>(${ast.getName()}${suffixes.msgAction()}<@Util.printTypeParameters ast false/>)
  <#elseif !noInputsForActions>
    <#assign port = inPorts[0]>
    <#assign type = port.getType()>
    <#if helper.getTypeHelper().isUnboxedChar(type)>(montiarc.rte.automaton.actions.CharAction)
    <#elseif helper.getTypeHelper().isUnboxedBoolean(type)>(montiarc.rte.automaton.actions.BooleanAction)
    <#elseif helper.getTypeHelper().isUnboxedByte(type)>(montiarc.rte.automaton.actions.ByteAction)
    <#elseif helper.getTypeHelper().isUnboxedShort(type)>(montiarc.rte.automaton.actions.ShortAction)
    <#elseif helper.getTypeHelper().isUnboxedInt(type)>(montiarc.rte.automaton.actions.IntAction)
    <#elseif helper.getTypeHelper().isUnboxedLong(type)>(montiarc.rte.automaton.actions.LongAction)
    <#elseif helper.getTypeHelper().isUnboxedFloat(type)>(montiarc.rte.automaton.actions.FloatAction)
    <#elseif helper.getTypeHelper().isUnboxedDouble(type)>(montiarc.rte.automaton.actions.DoubleAction)</#if>
  </#if>
</#macro>
