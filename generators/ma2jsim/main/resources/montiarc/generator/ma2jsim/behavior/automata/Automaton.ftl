<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("variant")}

<#assign automaton = helper.getAutomatonBehavior(ast).get() />
<#assign isEvent = helper.isEventBased(automaton)/>

${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.Header.ftl", [automaton])}
{
<#if isEvent>
  ${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.EventBody.ftl", [automaton])}
<#else>
  ${tc.include("montiarc.generator.ma2jsim.behavior.automata.SyncBody.ftl")}
</#if>
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/Init.ftl", [automaton])}
}
