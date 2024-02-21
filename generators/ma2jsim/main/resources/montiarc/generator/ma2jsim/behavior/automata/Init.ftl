<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("automaton")}
@Override
public void init() {
<#if ast.getSymbol().isAtomic()>
    <#list helper.getAutomatonInitAction(automaton)>
        ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
        ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
        ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}
        ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
        <#items as initStatement>
            ${prettyPrinter.prettyprint(initStatement)}
        </#items>
        ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SendInitialTicksOnDelayedPorts.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
        ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}
    <#else>
        /* Could not determine init action from any of the automaton's initial states */
    </#list>
</#if>
}
