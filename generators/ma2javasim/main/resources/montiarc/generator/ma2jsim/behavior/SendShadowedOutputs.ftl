<#-- (c) https://github.com/MontiCore/monticore -->
<#-- AST IGNORED -->
${tc.signature("portSymbols")}
<#list portSymbols as port>
    <#if !port.getType().isPrimitive()>if(${port.getName()} != null) </#if>context.${prefixes.port()}${port.getName()}().send(${port.getName()});
</#list>
