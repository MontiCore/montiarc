<#-- (c) https://github.com/MontiCore/monticore -->
<#-- AST IGNORED -->
${tc.signature("portSymbols", "skipDelay")}
<#list portSymbols as port>
    <#if !port.getType().isPrimitive()>if(${port.getName()} != null)</#if>
    <#if skipDelay && port.isDelayed()>
        ((montiarc.rte.port.DelayedOutPort) context.${prefixes.port()}${port.getName()}()).sendWithoutDelay(${port.getName()});
    <#else>
        context.${prefixes.port()}${port.getName()}().send(${port.getName()});
    </#if>
</#list>
