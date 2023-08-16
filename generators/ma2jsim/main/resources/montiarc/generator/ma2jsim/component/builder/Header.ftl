<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
public <#if isTop>abstract</#if> class ${ast.getName()}${suffixes.component()}${suffixes.builder()}<#if isTop>${suffixes.top()}</#if>