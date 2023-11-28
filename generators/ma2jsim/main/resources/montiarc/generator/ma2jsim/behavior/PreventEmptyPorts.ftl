<#-- (c) https://github.com/MontiCore/monticore -->
<#-- AST IGNORED -->
${tc.signature("portSymbols", "returnValue")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#list portSymbols>if(
  <#items as port>
    context.${prefixes.port()}${port.getName()}().isTickBlocked() || context.${prefixes.port()}${port.getName()}().isBufferEmpty()
  <#sep> || </#sep></#items>
  ) return ${returnValue};
</#list>
