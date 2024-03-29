<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("automaton", "transition", "shadowedInPorts")}
<#assign body = helper.getASTTransitionBody(transition)/>
new montiarc.rte.automaton.TransitionBuilder()
.setSource(${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}.${prefixes.state()}${transition.getSourceName()})
.setTarget(${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}.${prefixes.state()}${transition.getTargetName()})
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
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowInputs.ftl", [shadowedInPorts, true])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}


  <#assign commonSuperState = automaton.findCommonSuperstate(transition.getSourceNameDefinition(), transition.getTargetNameDefinition())!>
    <#-- Common superstate exists-->
    <#if commonSuperState != "">
      <#list automaton.gettrimlist(transition.getSourceNameDefinition(), commonSuperState) as state >
        <#if state?is_first>
          ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}.${prefixes.state()}${state.getName()}.exitSub();
        <#else>
          ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}.${prefixes.state()}${state.getName()}.exit();
        </#if>
      </#list>
        <#if body.isPresent() && body.get().isPresentTransitionAction()>
          ${prettyPrinter.prettyprint(body.get().getTransitionAction())}
        </#if>
          ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}.${prefixes.state()}${commonSuperState.getName()}.doAction();
      <#list automaton.gettrimlist(transition.getTargetNameDefinition(), commonSuperState) as state >
        <#if state?is_last>
          ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}.${prefixes.state()}${state.getName()}.enterWithSub();
        <#else>
          ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}.${prefixes.state()}${state.getName()}.enter();
        </#if>
      </#list>
    <#else> <#-- No common superstate -->
    <#list automaton.findPath(transition.getSourceNameDefinition())as state>
      <#if state?is_first>
        ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}.${prefixes.state()}${state.getName()}.exitSub();
      <#else>
        ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}.${prefixes.state()}${state.getName()}.exit();
      </#if>
    </#list>
      <#if body.isPresent() && body.get().isPresentTransitionAction()>
        ${prettyPrinter.prettyprint(body.get().getTransitionAction())}
      </#if>
    <#list automaton.findPath(transition.getTargetNameDefinition())?reverse as state>
      <#if state?is_last>
        ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}.${prefixes.state()}${state.getName()}.enterWithSub();
      <#else>
        ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}.${prefixes.state()}${state.getName()}.enter();
      </#if>
    </#list>
    </#if>
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}
})
.build()