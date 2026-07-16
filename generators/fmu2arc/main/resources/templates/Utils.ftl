<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Utils.ftl -->
<#assign variables = fmu.getModelDescription().getModelVariables().getVariables()>
<#assign eventClass = fmu.getName() + "Events">
<#assign syncMsgClass = fmu.getName() + "SyncMsg">
<#assign ContextClass = fmu.getName() + "Context">
<#assign inputVars = []>
<#list variables as v>
  <#if (v.getCausality()?? && v.getCausality().toString() == "INPUT") || ((v.getCausality()?? && v.getCausality().toString() == "PARAMETER") && (v.getVariability()?? && v.getVariability().toString() == "TUNABLE"))>
    <#assign inputVars = inputVars + [v]>
  </#if>
</#list>
<#assign syncInputVars = []>
<#list variables as v>
  <#if (v.getCausality()?? && v.getCausality().toString() == "INPUT")>
    <#assign syncInputVars = syncInputVars + [v]>
  </#if>
</#list>
<#assign tunParamVars = []>
<#list variables as v>
  <#if ((v.getCausality()?? && v.getCausality().toString() == "PARAMETER") && (v.getVariability()?? && v.getVariability().toString() == "TUNABLE"))>
    <#assign tunParamVars = tunParamVars + [v]>
  </#if>
</#list>
<#assign outputVars = []>
<#list variables as v>
  <#if v.getCausality()?? && v.getCausality().toString() == "OUTPUT">
    <#assign outputVars = outputVars + [v]>
  </#if>
</#list>
<#assign fixedParamVars = []>
<#list variables as v>
  <#if v.getCausality()?? && v.getCausality().toString() == "PARAMETER" && (v.getVariability()?? && v.getVariability().toString() == "FIXED")>
    <#assign fixedParamVars = fixedParamVars + [v]>
  </#if>
</#list>
<#assign InOutVars = inputVars + outputVars>
<#assign p = "">
<#if packageName != "">
  <#assign p = "package " + packageName + ";">
</#if>
<#function sanitizeName varName>
  <#return varName?replace("[^a-zA-Z0-9_]", "_", "r")>
</#function>
<#function toJavaType fmiType>
  <#switch fmiType.toString()>
    <#case "REAL"><#return "double">
    <#case "INTEGER"><#return "int">
    <#case "BOOLEAN"><#return "boolean">
    <#case "STRING"><#return "String">
    <#case "ENUMERATION"><#return "int">
    <#default><#return "Object">
  </#switch>
</#function>
<#function toBoxedJavaType fmiType>
  <#switch fmiType.toString()>
    <#case "REAL"><#return "Double">
    <#case "INTEGER"><#return "Integer">
    <#case "BOOLEAN"><#return "Boolean">
    <#case "STRING"><#return "String">
    <#case "ENUMERATION"><#return "Integer">
    <#default><#return "Object">
  </#switch>
</#function>
<#function toFmi4jType fmiType>
  <#switch fmiType.toString()>
    <#case "REAL"><#return "Real">
    <#case "INTEGER"><#return "Integer">
    <#case "BOOLEAN"><#return "Boolean">
    <#case "STRING"><#return "String">
    <#case "ENUMERATION"><#return "Integer">
    <#default><#return "Real">
  </#switch>
</#function>

<#function getJavaDefault fmiType>
  <#switch fmiType.toString()>
    <#case "BOOLEAN"><#return "false">
    <#case "STRING"><#return "null">
    <#case "REAL"><#return "0.0">
    <#case "INTEGER"><#return "0">
    <#case "ENUMERATION"><#return "0">
    <#default><#return "0">
  </#switch>
</#function>
