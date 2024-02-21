<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTMACompilationUnit ast -->
${tc.signature("ast", "isTop", "variant")}
/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

<#assign automaton = helper.getAutomatonBehavior(variant.getAstNode()).get() />
<#assign isEvent = helper.isEventBased(automaton)/>

${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.Header.ftl", variant.getAstNode(), [isTop, automaton])}
{
<#if isEvent>
  ${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.EventBody.ftl", variant.getAstNode(), [isTop, automaton])}
<#else>
  ${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.SyncBody.ftl", variant.getAstNode(), [isTop])}
</#if>
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/Init.ftl", variant.getAstNode(), [automaton])}
}
