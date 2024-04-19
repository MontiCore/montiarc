<#-- (c) https://github.com/MontiCore/monticore -->

<#-- SymTypeExpression type -->
<#macro getPortTypeString type>
  <#if type.isPrimitive() && type.isNumericType()>Number<#t>
  <#else><@getTypeString type true/><#t></#if>
</#macro>

<#-- SymTypeExpression type -->
<#macro getTypeString type boxPrimitives=false>
    <#if type.isPrimitive()><#t>
        <#if boxPrimitives>${type.getBoxedPrimitiveName()}<#t>
        <#else>${type.getPrimitiveName()}</#if><#t>
    <#elseif type.isTypeVariable()>${type.print()}<#t>
    <#else>${type.printFullName()}<#t>
    </#if>
</#macro>

<#-- CompTypeExpression type -->
<#macro getCompTypeString type suffix="">
    <#if type.getTypeBindingsAsList()?has_content>
        ${type.getTypeInfo().getFullName()}${suffix}<<#list type.getTypeBindingsAsList() as arg>${arg.printFullName()}<#sep>, </#sep></#list>>
    <#else>${type.printFullName()}${suffix}</#if>
</#macro>

<#macro getStaticPortInterface portSym>
    <#if portSym.isIncoming()>
    montiarc.rte.port.ITimeAwareInPort
    <#else>
    montiarc.rte.port.TimeAwareOutPort
    </#if>
</#macro>

<#macro printTypeParameters astComponentType printBounds=true>
    <#if helper.isGenericComponent(astComponentType)>
        <#list astComponentType.getHead().getArcTypeParameterList()>
            ${"<"}
            <#items as param><@printTypeParameter param printBounds/><#sep>, </#sep></#items>
            ${">"}
        </#list>
    </#if>
</#macro>

<#macro printTypeParameter arcTypeParameter printBounds>
${arcTypeParameter.getName()}<#if printBounds><#list arcTypeParameter.getUpperBoundList()> extends <#items as bound>${bound.printType()}<#sep> & </#sep></#items></#list></#if>
</#macro>