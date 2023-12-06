<#-- (c) https://github.com/MontiCore/monticore -->
<#-- AST IGNORED -->
${tc.signature("fields")}
<#list fields as field>
    <#if !field.getSymbol().getType().isPrimitive()>if(${field.getName()} != null)</#if>
  context.${prefixes.setterMethod()}${prefixes.field()}${field.getName()}(${field.getName()});
</#list>
