<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

interface ${ast.getName()}${suffixes.fields()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>{

  <#list ast.getSymbol().getFields() as field>
    <@Util.getTypeString field.getType()/> ${prefixes.field()}${field.getName()}${helper.fieldVariantSuffix(ast, field)}();
    void ${prefixes.setterMethod()}${prefixes.field()}${field.getName()}${helper.fieldVariantSuffix(ast, field)}(<@Util.getTypeString field.getType()/> value);
  </#list>
}