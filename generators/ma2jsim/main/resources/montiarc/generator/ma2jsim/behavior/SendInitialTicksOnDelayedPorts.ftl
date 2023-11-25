<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("portSymbols")}
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#list portSymbols as port>
  <#if port.isDelayed()>
    context.${prefixes.port()}${port.getName()}().<@MethodNames.sendTick/>();
  </#if>
</#list>
