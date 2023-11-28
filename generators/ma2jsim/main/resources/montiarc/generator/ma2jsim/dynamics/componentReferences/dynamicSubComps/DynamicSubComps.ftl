<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#list helper.getInstancesFromModes(helper.getModeAutomaton(ast).get()) as mode, subComps>
    <#list subComps as sub>
        ${tc.includeArgs("montiarc.generator.ma2jsim.dynamics.componentReferences.dynamicSubComps.Field.ftl", sub, [mode])}

        ${tc.includeArgs("montiarc.generator.ma2jsim.dynamics.componentReferences.dynamicSubComps.Getter.ftl", sub, [mode])}
    </#list>
</#list>

protected void <@MethodNames.dynSubCompSetup/>() {
<#list helper.getInstancesFromModes(helper.getModeAutomaton(ast).get()) as mode, subComps>
    <#list subComps as sub>
        ${tc.includeArgs("montiarc.generator.ma2jsim.dynamics.componentReferences.dynamicSubComps.Setup.ftl", sub, [mode])}
    </#list>
</#list>
}