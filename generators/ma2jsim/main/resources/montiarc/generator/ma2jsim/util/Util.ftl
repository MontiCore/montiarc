<#-- (c) https://github.com/MontiCore/monticore -->

<#-- SymTypeExpression type -->
<#macro getTypeString type>
    <#if type.isPrimitive()>${type.getBoxedPrimitiveName()}<#elseif type.isTypeVariable()>${type.print()}<#else>${type.printFullName()}</#if>
</#macro>

<#-- SymTypeExpression type -->
<#macro getCompTypeString type>
    ${type.printFullName()}
</#macro>

<#macro getStaticPortClass portSym atomic>
    <#assign direction><#if portSym.isIncoming()>In<#else>Out</#if></#assign>
    <#assign timing><#if portSym.getTiming().matches(timing_untimed)>Unaware<#else>Aware</#if></#assign>
    montiarc.rte.port.
    <#if !atomic>
        Time${timing}PortForward
    <#else>
        Time${timing}${direction}Port
    </#if>
</#macro>