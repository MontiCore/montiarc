<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("variant")}

<#assign automaton = helper.getAutomatonBehavior(ast).get() />

${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.Header.ftl", [automaton])}
{
  ${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.Body.ftl", [automaton])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/Init.ftl", [automaton])}
}
