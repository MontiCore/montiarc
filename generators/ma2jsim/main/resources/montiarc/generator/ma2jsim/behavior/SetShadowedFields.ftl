<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("fields")}
<#list fields as field>
    <#if !field.getSymbol().getType().isPrimitive()>if(${field.getName()} != null)</#if>
  context.${prefixes.setterMethod()}${prefixes.field()}${field.getName()}${helper.fieldVariantSuffix(ast, field.getSymbol())}(${field.getName()});
</#list>
