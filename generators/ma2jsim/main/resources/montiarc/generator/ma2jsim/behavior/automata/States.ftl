<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop", "variant")}

<#assign automaton = helper.getAutomatonBehavior(ast).get() />
public abstract class ${ast.getName()}${suffixes.states()}${helper.variantSuffix(variant)}<#if isTop>${suffixes.top()}</#if> {
  <#list helper.streamToList(automaton.streamStates()) as state>
    public static montiarc.rte.automaton.State ${prefixes.state()}${state.getName()} = new montiarc.rte.automaton.State("${state.getName()}");
  </#list>
}
