<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
public interface ${ast.getName()}${suffixes.context()}<#if isTop>${suffixes.top()}</#if>
  extends ${ast.getName()}${suffixes.input()}, ${ast.getName()}${suffixes.output()} {
  <#if helper.getAutomatonBehavior(ast).isPresent()>
    ${ast.getName()}${suffixes.automaton()} getBehavior();
  </#if>
}