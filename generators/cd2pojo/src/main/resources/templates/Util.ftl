<#-- (c) https://github.com/MontiCore/monticore -->

<#function getType symTypeExpression>
    <#local fullTypeName>${symTypeExpression.getTypeInfo().getName()}</#local>
    <#if fullTypeName == "voidType"><#local fullTypeName = "void"></#if>
    <#return fullTypeName>
</#function>