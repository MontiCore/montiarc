<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast-->
${tc.signature("field")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

@Override
public <@Util.getTypeString field.getType()/> ${prefixes.field()}${field.getName()}${helper.fieldVariantSuffix(ast, field)}() {
<#if existenceConditions?has_content>
  ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}
  if(${prettyPrinter.prettyprint(existenceConditions)}) {
</#if>
  return this.${prefixes.field()}${field.getName()}${helper.fieldVariantSuffix(ast, field)};
<#if existenceConditions?has_content>
  } else throw new RuntimeException(
  "Field ${field.getName()} is not available in component " + getName()
  + " under the given feature configuration.");
</#if>
}
