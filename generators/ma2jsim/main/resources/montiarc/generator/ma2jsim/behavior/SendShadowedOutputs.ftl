<#-- (c) https://github.com/MontiCore/monticore -->
<#-- AST IGNORED -->
${tc.signature("portSymbols", "skipDelay")}
<#list portSymbols as port>
    <#if !port.getType().isPrimitive()>if(${port.getName()} != null)</#if>
    <#if skipDelay && port.isDelayed()>
        ((montiarc.rte.port.DelayedOutPort) context.${prefixes.port()}${port.getName()}()).sendWithoutDelay(<@portValue port/>);
    <#else>
        context.${prefixes.port()}${port.getName()}().send(<@portValue port/>);
    </#if>
</#list>

<#--
  - Chars must first be converted to ints so that they can be sent over ports of type Number
  - Signature: PortSymbol port
  -->
<#macro portValue port>
  <#if helper.isUnboxedChar(port.getType())>((int) ${port.getName()})<#t>
  <#else>${port.getName()}<#t></#if>
</#macro>
