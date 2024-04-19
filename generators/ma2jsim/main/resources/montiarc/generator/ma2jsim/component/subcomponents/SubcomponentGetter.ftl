<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast-->
<#-- SubcomponentSymbol subcomponentSym, String modeName (may be empty: "") -->
${tc.signature("subcomponentSym", "modeName")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign existenceConditions = helper.getExistenceCondition(ast, subcomponentSym)/>
<#assign subCompType><@Util.getCompTypeString subcomponentSym.getType() suffixes.component()/></#assign>
<#assign modeNamePart>${modeName}<#if modeName?has_content>_</#if></#assign>
<#assign subCompName>${prefixes.subcomp()}${modeNamePart}${subcomponentSym.getName()}${helper.subcomponentVariantSuffix(ast, subcomponentSym)}</#assign>

protected ${subCompType} ${subCompName}() {
<#if existenceConditions?has_content>
    ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}
    if(${prettyPrinter.prettyprint(existenceConditions)}) {
</#if>
return this.${subCompName};
<#if existenceConditions?has_content>
    } else throw new java.lang.RuntimeException(
    "Subcomponent ${subcomponentSym.getName()} is not available in component " + getName()
    + " under the given feature configuration.");
</#if>
}