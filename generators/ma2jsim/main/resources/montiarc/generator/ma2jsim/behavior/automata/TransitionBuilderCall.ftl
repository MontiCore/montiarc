<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("automaton", "transition", "shadowedInPorts")}
<#assign body = helper.getASTTransitionBody(transition)/>
new montiarc.rte.automaton.TransitionBuilder()
.setSource(states.${prefixes.state()}${transition.getSourceName()})
.setTarget(states.${prefixes.state()}${transition.getTargetName()})
.setGuard(() ->
<#if body.isPresent() && body.get().isPresentPre()>
    {
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/PreventEmptyPorts.ftl", [shadowedInPorts, "false"])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowInputs.ftl", [shadowedInPorts, false])}
    return ${prettyPrinter.prettyprint(body.get().getPre())};
    }
<#else>
    true
</#if>
)
.setAction(() -> {
  <#assign commonSuperstate = automaton.findCommonSuperstate(transition.getSourceNameDefinition(), transition.getTargetNameDefinition())!>
  <#-- Common superstate exists-->
  <#if commonSuperstate != "">
    <#list automaton.gettrimlist(transition.getSourceNameDefinition(), commonSuperstate) as state >
      <#if state?is_first>
        states.${prefixes.state()}${state.getName()}.exitSub(state);
      <#else>
        states.${prefixes.state()}${state.getName()}.exit();
      </#if>
    </#list>

    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowInputs.ftl", [shadowedInPorts, true])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
    <#if body.isPresent() && body.get().isPresentTransitionAction()>
      ${prettyPrinter.prettyprint(body.get().getTransitionAction())}
    </#if>
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}

    states.${prefixes.state()}${commonSuperstate.getName()}.doAction();
    <#list automaton.gettrimlist(transition.getTargetNameDefinition(), commonSuperstate) as state >
      <#if state?is_last>
        states.${prefixes.state()}${state.getName()}.enterWithSub();
      <#else>
        states.${prefixes.state()}${state.getName()}.enter();
      </#if>
    </#list>
  <#else> <#-- No common superstate -->
    <#list automaton.findPath(transition.getSourceNameDefinition()) as state>
      <#if state?is_first>
        states.${prefixes.state()}${state.getName()}.exitSub(state);
      <#else>
        states.${prefixes.state()}${state.getName()}.exit();
      </#if>
    </#list>

    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowInputs.ftl", [shadowedInPorts, true])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
    <#if body.isPresent() && body.get().isPresentTransitionAction()>
      ${prettyPrinter.prettyprint(body.get().getTransitionAction())}
    </#if>
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}

    <#list automaton.findPath(transition.getTargetNameDefinition())?reverse as state>
      <#if state?is_last>
        states.${prefixes.state()}${state.getName()}.enterWithSub();
      <#else>
        states.${prefixes.state()}${state.getName()}.enter();
      </#if>
    </#list>
  </#if>
})
.build()