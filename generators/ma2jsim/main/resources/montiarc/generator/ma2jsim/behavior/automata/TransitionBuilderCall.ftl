<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("transition" "shadowedInPorts")}
<#assign body = helper.getASTTransitionBody(transition)/>
new montiarc.rte.automaton.TransitionBuilder()
.setSource(${ast.getName()}${suffixes.states()}.${prefixes.state()}${transition.getSourceName()})
.setTarget(${ast.getName()}${suffixes.states()}.${prefixes.state()}${transition.getTargetName()})
.setGuard(() ->
<#if body.isPresent() && body.get().isPresentPre()>
    {
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/PreventEmptyPorts.ftl", [shadowedInPorts, "false"])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
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
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
  <#if body.isPresent() && body.get().isPresentTransitionAction()>
  ${prettyPrinter.prettyprint(body.get().getTransitionAction())}
  </#if>
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}
})
.build()