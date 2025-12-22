<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("fields")}
<#list fields as field>
    <#if !field.getSymbol().getType().isPrimitive()>if(${field.getName()} != null)</#if>
  context.${prefixes.setterMethod()}${prefixes.field()}${field.getName()}${helper.getVariantHelper().fieldVariantSuffix(ast, field.getSymbol())}(${field.getName()});
</#list>
