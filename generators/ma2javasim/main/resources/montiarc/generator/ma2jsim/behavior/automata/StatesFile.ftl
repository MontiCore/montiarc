<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop")}
/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

${tc.include("montiarc.generator.Import.ftl", ast.getImportStatementList())}

<#assign comp = ast.getComponentType()/>
<#assign automaton = helper.getAutomatonBehavior(comp).get()/>
public abstract class ${comp.getName()}${suffixes.states()}<#if isTop>${suffixes.top()}</#if> {
  <#list helper.streamToList(automaton.streamStates()) as state>
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/State.ftl", comp, [state.getName()])}
  </#list>
}
