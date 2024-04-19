<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#-- Shadow parameters -->
<#list ast.getHead().getArcParameterList() as param>
  final <@Util.getTypeString param.getSymbol().getType()/> ${param.getName()} =
  getContext().${prefixes.parameter()}${param.getName()}();
</#list>
<#-- Shadow features -->
<#list helper.getFeatures(ast) as feature>
  final boolean ${feature.getName()} = getContext() .${prefixes.feature()}${feature.getName()}();
</#list>
