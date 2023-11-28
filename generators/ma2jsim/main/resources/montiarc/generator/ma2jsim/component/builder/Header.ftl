<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
public <#if isTop>abstract</#if> class ${ast.getName()}${suffixes.component()}${suffixes.builder()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>