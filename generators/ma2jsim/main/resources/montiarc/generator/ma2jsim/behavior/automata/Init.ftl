<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
public void init() {
<#if ast.getSymbol().isAtomic()>

    <#list helper.getAutomatonInitAction(ast)>
        ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
        <#items as initStatement>
            ${prettyPrinter.prettyprint(initStatement)}
        </#items>
        ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SendShadowedOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
        ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SendInitialTicksOnDelayedPorts.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
    <#else>
        /* Could not determine init action from any of the automaton's initial states */
    </#list>
</#if>
}
