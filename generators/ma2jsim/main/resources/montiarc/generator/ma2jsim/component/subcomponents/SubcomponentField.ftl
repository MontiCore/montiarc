<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast-->
${tc.signature("subcomponentSym")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
protected <@Util.getCompTypeString subcomponentSym.getType() suffixes.component()/> ${prefixes.subcomp()}${subcomponentSym.getName()}${helper.subcomponentVariantSuffix(ast, subcomponentSym)};