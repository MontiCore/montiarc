<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("fields")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#list fields as field>
  <@Util.getTypeString field.getSymbol().getType()/> ${field.getName()} =
  context.${prefixes.field()}${field.getName()}${helper.getVariantHelper().fieldVariantSuffix(ast, field.getSymbol())}();
</#list>
