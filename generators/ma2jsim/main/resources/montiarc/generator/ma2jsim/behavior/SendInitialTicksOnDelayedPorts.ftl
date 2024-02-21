<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("portSymbols")}
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#list portSymbols as port>
  <#if port.isDelayed()>
    context.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)}().<@MethodNames.sendTick/>();
  </#if>
</#list>
