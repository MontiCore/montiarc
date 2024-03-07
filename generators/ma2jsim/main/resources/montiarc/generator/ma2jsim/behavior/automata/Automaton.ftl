<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop", "variant")}

<#assign automaton = helper.getAutomatonBehavior(ast).get() />
<#assign isEvent = helper.isEventBased(automaton)/>
import montiarc.rte.automaton.Automaton;

${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.Header.ftl", [isTop, automaton])}
{
<#if isEvent>
  ${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.EventBody.ftl", [isTop, automaton])}
<#else>
  ${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.SyncBody.ftl", [isTop])}
</#if>
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/Init.ftl", [automaton])}
}
