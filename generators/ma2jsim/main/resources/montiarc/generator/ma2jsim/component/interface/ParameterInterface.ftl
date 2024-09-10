<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

interface ${ast.getName()}${suffixes.parameters()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>{

<#list ast.getHead().getArcParameterList() as param>
    <@Util.getTypeString param.getSymbol().getType()/> ${prefixes.parameter()}${param.getName()}();
</#list>
}