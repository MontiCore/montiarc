<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast-->
${tc.signature("subcomponentSym")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign existenceConditions = helper.getExistenceCondition(ast, subcomponentSym)/>
protected <@Util.getCompTypeString subcomponentSym.getType() suffixes.component()/> ${prefixes.subcomp()}${subcomponentSym.getName()}${helper.subcomponentVariantSuffix(ast, subcomponentSym)}() {
<#if existenceConditions?has_content>
    ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}
    if(${prettyPrinter.prettyprint(existenceConditions)}) {
</#if>
return this.${prefixes.subcomp()}${subcomponentSym.getName()}${helper.subcomponentVariantSuffix(ast, subcomponentSym)};
<#if existenceConditions?has_content>
    } else throw new java.lang.RuntimeException(
    "Subcomponent ${subcomponentSym.getName()} is not available in component " + getName()
    + " under the given feature configuration.");
</#if>
}