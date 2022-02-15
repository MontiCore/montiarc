<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Prints the component's configuration parameters as a comma separated list. -->
<#macro printConfigurationParametersAsList comp>
    <#list comp.getParameters() as param>
        ${param.getType().print()} ${param.getName()}<#t><#sep>, </#sep><#t>
    </#list>
</#macro>

<#-- Prints the component's imports -->
<#macro printImportsFromAST comp helper>
    <#list helper.getImports(comp) as _import>
      import ${_import.getStatement()}<#if _import.isStar()>.*</#if>;
    </#list>
</#macro>

<#macro printImportsForInnerComponents comp>
    <#list comp.getInnerComponents() as inner>
      import <@componentClassFQN comp=inner/>;
    </#list>
</#macro>

<#-- Prints a member of given visibility name and type -->
<#macro printMember type name visibility>
    <#lt>${visibility} ${type} ${name};
</#macro>

<#-- Prints members for configuration parameters. -->
<#macro printConfigParameters comp>
    <#list comp.getParameters() as param>
        <#lt>  <@printMember type=param.getType().print() name=param.getName() visibility="private final"/>
    </#list>
</#macro>

<#-- Prints members for variables -->
<#macro printVariables comp compHelper>
    <#list compHelper.getComponentVariables(comp) as variable>
        <#assign visibility="protected">
        <#assign type=variable.getType().print()>
        <#assign name=variable.getName()>
        <#if compHelper.hasInitializerExpression(variable)>
            <#assign initial=compHelper.printExpression(compHelper.getInitializerExpression(variable))>
            ${visibility} ${type} ${name} = ${initial};
        <#else>
            ${visibility} ${type} ${name};
        </#if>
    </#list>
</#macro>

<#-- Prints formal parameters of a component. -->
<#macro printFormalTypeParameters comp>
    <#if comp.hasTypeParameter()>
<<#list comp.getTypeParameters() as generic>${generic.getName()}<#sep>, </#sep></#list>><#t>
    </#if>
</#macro>

<#macro forEachSubcomponent comp>
  <#list comp.getSubComponents() as subcomponent>
    this.${subcomponent.getName()}<#nested>
    </#list>
</#macro>

<#-- macros for fully qualified names of (generated or handwritten) classes for a given ComponentTypeSymbol -->
<#macro componentClassFQN comp>
  ${comp.getFullName()}<#t>
</#macro>

<#macro componentInputClassFQN comp>
  <@componentClassFQNWithSuffix comp=comp suffix="Input"/><#t>
</#macro>

<#macro componentResultClassFQN comp>
  <@componentClassFQNWithSuffix comp=comp suffix="Result"/><#t>
</#macro>

<#macro componentImplClassFQN comp>
  <@componentClassFQNWithSuffix comp=comp suffix="Impl"/><#t>
</#macro>

<#macro componentClassFQNWithSuffix comp suffix>
    <#if comp.isInnerComponent()>
        <@componentClassFQNWithSuffix comp=comp.getOuterComponent().get() suffix=suffix/>.${comp.getName()}${suffix}<#t>
    <#else>
        ${comp.getFullName()}${suffix}<#t>
    </#if>
</#macro>

<#-- macros for fully qualified names of superclasses of generated classes for a given ComponentTypeSymbol -->
<#macro componentSuperClassFQN comp>
    <@componentClassFQN comp=comp.getParentInfo()/>
</#macro>

<#macro componentInputSuperClassFQN comp>
    <@componentInputClassFQN comp = comp.getParentInfo()/>
</#macro>

<#macro componentResultSuperClassFQN comp>
    <@componentResultClassFQN comp=comp.getParentInfo()/>
</#macro>

<#macro componentImplSuperClassFQN comp>
    <@componentImplClassFQN comp=comp.getParentInfo/>
</#macro>
