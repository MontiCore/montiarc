<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->

<#-- SymTypeExpression type -->
<#macro getPortTypeString type>
  <@getTypeString type true/><#t>
</#macro>

<#-- SymTypeExpression type -->
<#macro getTypeString type boxPrimitives=false>
  ${javaPrinter.generateCode(type, boxPrimitives)}
</#macro>

<#-- CompKindExpression type -->
<#macro getCompTypeString type suffix="">
    <#if type.isGenericComponentType() && type.getTypeBindingsAsList()?has_content>
        ${type.getTypeInfo().getFullName()}${suffix}<<#list type.getTypeBindingsAsList() as arg><@getPortTypeString arg/><#sep>, </#sep></#list>>
    <#else>${type.printFullName()}${suffix}</#if>
</#macro>

<#macro getStaticPortInterface portSym>
    <#if portSym.isIncoming()>
    montiarc.rte.port.InPort
    <#else>
    montiarc.rte.port.OutPort
    </#if>
</#macro>

<#macro printTypeParameters astComponentType printBounds=true>
    <#if helper.getComponentHelper().isGenericComponent(astComponentType)>
        <#list astComponentType.getSymbol().getTypeParameters()>
            ${"<"}
            <#items as param><@printTypeParameter param printBounds/><#sep>, </#sep></#items>
            ${">"}
        </#list>
    </#if>
</#macro>

<#-- TypeVarSymbol typeParameter -->
<#macro printTypeParameter typeParameter printBounds>
${typeParameter.getName()}<#if printBounds><#list typeParameter.getSuperTypesList()> extends <#items as bound><@getTypeString bound true/><#sep> & </#sep></#items></#list></#if>
</#macro>
