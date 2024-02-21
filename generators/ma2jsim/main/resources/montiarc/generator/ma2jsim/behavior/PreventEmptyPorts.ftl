<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("portSymbols", "returnValue")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#list portSymbols>if(
  <#items as port>
    context.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)}().isTickBlocked() || context.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)}().isBufferEmpty()
  <#sep> || </#sep></#items>
  ) return ${returnValue};
</#list>
