<#-- (c) https://github.com/MontiCore/monticore -->

<#-- SymTypeExpression type -->
<#macro getTypeString type boxPrimitives=false>
    <#if type.isPrimitive()><#t>
        <#if boxPrimitives>${type.getBoxedPrimitiveName()}<#t>
        <#else>${type.getPrimitiveName()}</#if><#t>
    <#elseif type.isTypeVariable()>${type.print()}<#t>
    <#else>${type.printFullName()}<#t>
    </#if>
</#macro>

<#-- SymTypeExpression type -->
<#macro getCompTypeString type>
    ${type.printFullName()}
</#macro>

<#macro getStaticPortClass portSym atomic>
    <#assign direction><#if portSym.isIncoming()>In<#else>Out</#if></#assign>
    montiarc.rte.port.
    <#if !atomic>
        TimeAwarePortForward
    <#else>
        TimeAware${direction}Port
    </#if>
</#macro>