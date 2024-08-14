<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("portSym")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign existenceConditions = helper.getExistenceCondition(ast, portSym)/>

public <@Util.getStaticPortInterface portSym/><<@Util.getPortTypeString portSym.getType()/>>
  ${prefixes.port()}${portSym.getName()}${helper.portVariantSuffix(ast, portSym)}() {
<#if existenceConditions?has_content>
  ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

  if(${prettyPrinter.prettyprint(existenceConditions)}) {
</#if>
  return this.${prefixes.port()}${portSym.getName()}${helper.portVariantSuffix(ast, portSym)};
<#if existenceConditions?has_content>
  } else throw new RuntimeException(
  "Port ${portSym.getName()} is not available in component " + getName()
  + " under the given feature configuration.");
</#if>
}