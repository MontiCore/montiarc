<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign automaton = helper.getModeAutomaton(ast).get()/>
<#assign isEvent = helper.isEventBased(automaton)/>

${tc.includeArgs("montiarc.generator.ma2jsim.dynamics.modeAutomaton.Header.ftl", [isTop])}
{
protected ${ast.getName()}${suffixes.component()}<@Util.printTypeParameters ast false/> context;

<#if isEvent>
    ${tc.includeArgs("montiarc.generator.ma2jsim.dynamics.modeAutomaton.EventBody.ftl", [isTop])}
<#else>
    ${tc.includeArgs("montiarc.generator.ma2jsim.dynamics.modeAutomaton.SyncBody.ftl", [isTop])}
</#if>
${tc.include("montiarc/generator/ma2jsim/dynamics/modeAutomaton/Init.ftl")}
}
