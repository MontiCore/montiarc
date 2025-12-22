<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

@de.se_rwth.commons.Generated("montiarc.generators.ma2jsim")
interface ${ast.getName()}${suffixes.fields()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>{

  <#list ast.getSymbol().getFields() as field>
    <@Util.getTypeString field.getType()/> ${prefixes.field()}${field.getName()}${helper.getVariantHelper().fieldVariantSuffix(ast, field)}();
    void ${prefixes.setterMethod()}${prefixes.field()}${field.getName()}${helper.getVariantHelper().fieldVariantSuffix(ast, field)}(<@Util.getTypeString field.getType()/> value);
  </#list>
}
