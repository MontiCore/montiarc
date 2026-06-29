<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("field")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

@Override
public <@Util.getTypeString field.getType()/> ${prefixes.field()}${field.getName()}${helper.getVariantHelper().fieldVariantSuffix(ast, field)}() {
<#if existenceConditions?has_content>
  ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}
  if(${javaPrinter.generateCode(existenceConditions)}) {
</#if>
  return this.${prefixes.field()}${field.getName()}${helper.getVariantHelper().fieldVariantSuffix(ast, field)};
<#if existenceConditions?has_content>
  } else throw new RuntimeException(
  "Field ${field.getName()} is not available in component " + getName()
  + " under the given feature configuration.");
</#if>
}
