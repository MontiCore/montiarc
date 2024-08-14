<#-- (c) https://github.com/MontiCore/monticore -->

<#-- SymTypeExpression type -->
<#macro getPortTypeString type>
  <#if type.isPrimitive() && type.isNumericType()>Number<#t>
  <#else><@getTypeString type true/><#t></#if>
</#macro>

<#-- SymTypeExpression type -->
<#macro getTypeString type boxPrimitives=false>
  ${prettyPrinter.prettyprint(type, boxPrimitives)}
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
        <#list astComponentType.getSymbol().getTypeParameters()>
            ${"<"}
            <#items as param><@printTypeParameter param printBounds/><#sep>, </#sep></#items>
            ${">"}
        </#list>
    </#if>
</#macro>

<#-- TypeVarSymbol arcTypeParameter -->
<#macro printTypeParameter arcTypeParameter printBounds>
${arcTypeParameter.getName()}<#if printBounds><#list arcTypeParameter.getSuperTypesList()> extends <#items as bound><@getTypeString bound true/><#sep> & </#sep></#items></#list></#if>
</#macro>

<#-- SymTypeExpression type -->
<#macro unboxNumbersSuffix type>
    <#if helper.isUnboxedChar(type)>.intValue()
    <#elseif helper.isUnboxedByte(type)>.byteValue()
    <#elseif helper.isUnboxedShort(type)>.shortValue()
    <#elseif helper.isUnboxedInt(type)>.intValue()
    <#elseif helper.isUnboxedLong(type)>.longValue()
    <#elseif helper.isUnboxedFloat(type)>.floatValue()
    <#elseif helper.isUnboxedDouble(type)>.doubleValue()</#if><#t>
</#macro>

<#-- SymTypeExpression type -->
<#macro unboxChar type>
    <#if helper.isUnboxedChar(type)>(char)</#if><#t>
</#macro>
