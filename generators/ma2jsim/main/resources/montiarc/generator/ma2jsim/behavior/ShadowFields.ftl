<#-- (c) https://github.com/MontiCore/monticore -->
<#-- AST IGNORED -->
${tc.signature("fields")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#list fields as field>
  <@Util.getTypeString field.getSymbol().getType()/> ${field.getName()} =
  context.${prefixes.field()}${field.getName()}();
</#list>
