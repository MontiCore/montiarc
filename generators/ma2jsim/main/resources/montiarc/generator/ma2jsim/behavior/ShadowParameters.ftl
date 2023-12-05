<#-- (c) https://github.com/MontiCore/monticore -->
<#-- AST IGNORED -->
${tc.signature("parameters")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#list parameters as param>
    <@Util.getTypeString param.getSymbol().getType()/> ${param.getName()} =
  context.${prefixes.parameter()}${param.getName()}();
</#list>