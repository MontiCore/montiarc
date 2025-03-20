<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("variant")}

<#assign automaton = helper.getAutomatonBehavior(ast).get() />

${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.Header.ftl", [automaton])}
{
  ${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.Body.ftl", [automaton])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/Init.ftl", [automaton])}
}
