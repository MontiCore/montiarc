<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop", "variant")}
/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

<#assign automaton = helper.getAutomatonBehavior(variant.getAstNode()).get() />
<#assign comp = variant.getAstNode() />
public abstract class ${comp.getName()}${suffixes.states()}${helper.variantSuffix(variant)}<#if isTop>${suffixes.top()}</#if> {
  <#list helper.streamToList(automaton.streamStates()) as state>
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/State.ftl", comp, [state.getName()])}
  </#list>
}
