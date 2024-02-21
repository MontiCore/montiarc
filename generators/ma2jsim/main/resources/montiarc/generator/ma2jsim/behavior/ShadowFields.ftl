<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("fields")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#list fields as field>
  <@Util.getTypeString field.getSymbol().getType()/> ${field.getName()} =
  context.${prefixes.field()}${field.getName()}${helper.fieldVariantSuffix(ast, field.getSymbol())}();
</#list>
