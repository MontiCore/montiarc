<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
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
<#assign body = helper.getASTTransitionBody(transition)/>

<#assign lambdaArgs>
  <#if noInputsForActions>
  <#-- "in" is a reserved keywoard and by that not usable as name, e.g., for fields.
    -- by using it as the name for the lambda parameter, we thus we avoid name collisions
    -->
    in
  <#else><#list inPorts as inPort>${inPort.getName()}<#sep>, </#list> </#if>
</#assign>

<#assign guardSignature>
  <#if useSyncMsg>(${ast.getName()}${suffixes.msgGuard()}<@Util.printTypeParameters ast false/>)</#if> (${lambdaArgs})
</#assign>
<#assign actionSignature>
  <#if useSyncMsg>(${ast.getName()}${suffixes.msgAction()}<@Util.printTypeParameters ast false/>)</#if> (${lambdaArgs})
</#assign>

<#assign transitionMsgType>
  <#if noInputsForActions> montiarc.rte.automaton.NoInput
  <#elseif useSyncMsg>${ast.getName()}${suffixes.syncMsg()}<@Util.printTypeParameters ast false/>
  <#else><#assign port = inPorts[0]> <@Util.getTypeString port.getType() true/></#if>
</#assign>

new montiarc.rte.automaton.TransitionBuilder<${transitionMsgType}>()
  .setSource(states.${prefixes.state()}${transition.getSourceName()})
  .setTarget(states.${prefixes.state()}${transition.getTargetName()})
  .setGuard(<@guard guardSignature/>)
  .setAction(<@action actionSignature/>)
  .build()

<#macro guard signature> ${signature} ->
  <#if body.isPresent() && body.get().isPresentPre()>
    {
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}
      return ${prettyPrinter.prettyprint(body.get().getPre())};
    }
  <#else>
    true
  </#if>
</#macro>

<#macro action signature>
  ${signature} -> {
  <#-- Calculate whether there the current state is in a state hierarchy that also contains the target state -->
  <#assign commonSuperstate = automaton.findCommonSuperstate(transition.getSourceNameDefinition(), transition.getTargetNameDefinition())!>
  <#assign haveCommonSuperstate = commonSuperstate != "">

  <#-- 1. Execute exit actions -->
  <#assign statesToExit = haveCommonSuperstate?then(
    automaton.getAncestorsInbetween(transition.getSourceNameDefinition(), commonSuperstate),
    automaton.findPath(transition.getSourceNameDefinition())
  )>
  <#list statesToExit as state>
    <#if state?is_first>
        this.states.${prefixes.state()}${state.getName()}.exitSub(state);
    <#else>
        this.states.${prefixes.state()}${state.getName()}.exit();
    </#if>
  </#list>

  <#-- 2. Actual transition action -->
  <#if body.isPresent() && body.get().isPresentTransitionAction()>
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
    ${prettyPrinter.prettyprint(body.get().getTransitionAction())}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}
  </#if>


  <#-- 3. Do action (if a common superstate is not left -->
  <#if haveCommonSuperstate>
    this.states.${prefixes.state()}${commonSuperstate.getName()}.doAction();
  </#if>

  <#-- 4. Enter actions -->
  <#assign statesToEnter = haveCommonSuperstate?then(
    automaton.getAncestorsInbetween(transition.getTargetNameDefinition(), commonSuperstate),
    automaton.findPath(transition.getTargetNameDefinition())?reverse
  )>
  <#list statesToEnter as state>
    <#if state?is_last>
        this.states.${prefixes.state()}${state.getName()}.enterWithSub();
    <#else>
        this.states.${prefixes.state()}${state.getName()}.enter();
    </#if>
  </#list>
}
</#macro>