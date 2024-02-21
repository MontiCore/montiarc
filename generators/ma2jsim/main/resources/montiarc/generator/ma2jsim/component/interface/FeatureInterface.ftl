<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

interface ${ast.getName()}${suffixes.features()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>{

<#list helper.getFeatures(ast) as feature>
    boolean ${prefixes.feature()}${feature.getName()}();
</#list>
}