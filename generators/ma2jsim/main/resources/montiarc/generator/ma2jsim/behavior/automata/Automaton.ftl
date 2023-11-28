<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}

<#assign automaton = helper.getAutomatonBehavior(ast).get()/>
<#assign isEvent = helper.isEventBased(automaton)/>

${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.Header.ftl", ast, [isTop])}
{
<#if isEvent>
  ${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.EventBody.ftl", ast, [isTop])}
<#else>
  ${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.SyncBody.ftl", ast, [isTop])}
</#if>
  ${tc.include("montiarc/generator/ma2jsim/behavior/automata/Init.ftl")}
}
