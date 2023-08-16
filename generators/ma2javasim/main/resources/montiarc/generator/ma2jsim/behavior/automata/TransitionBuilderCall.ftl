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
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowInputs.ftl", [shadowedInPorts, false])}
    return ${prettyPrinter.prettyprint(body.get().getPre())};
    }
<#else>
    true
</#if>
)
.setAction(() -> {
<#if body.isPresent() && body.get().isPresentTransitionAction()>
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowInputs.ftl", [shadowedInPorts, true])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
    ${prettyPrinter.prettyprint(body.get().getTransitionAction())}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SendShadowedOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
</#if>
})
.build()