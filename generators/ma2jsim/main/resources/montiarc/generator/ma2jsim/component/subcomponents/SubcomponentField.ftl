<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#-- SubcomponentSymbol subcomponentSym, String modeName (may be empty: "") -->
${tc.signature("subcomponentSym", "modeName")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign type><@Util.getCompTypeString subcomponentSym.getType() suffixes.compImpl()/></#assign>
<#assign modeNamePart>${modeName}<#if modeName?has_content>_</#if></#assign>
<#assign name>${prefixes.subcomp()}${modeNamePart}${subcomponentSym.getName()}${helper.getVariantHelper().subcomponentVariantSuffix(ast, subcomponentSym)}</#assign>

protected ${type} ${name};