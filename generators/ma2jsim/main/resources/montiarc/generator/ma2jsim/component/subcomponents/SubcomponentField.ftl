<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast-->
<#-- SubcomponentSymbol subcomponentSym, String modeName (may be empty: "") -->
${tc.signature("subcomponentSym", "modeName")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign type><@Util.getCompTypeString subcomponentSym.getType() suffixes.component()/></#assign>
<#assign modeNamePart>${modeName}<#if modeName?has_content>_</#if></#assign>
<#assign name>${prefixes.subcomp()}${modeNamePart}${subcomponentSym.getName()}${helper.subcomponentVariantSuffix(ast, subcomponentSym)}</#assign>

protected ${type} ${name};