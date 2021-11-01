<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Prints member and getter for component's subcomponents. -->
<#macro printSubcomponentsWithGetter comp compHelper>
  <#assign subComponents = comp.getSubComponents()>
  <#list subComponents as subcomponent>
    <#assign type = compHelper.getSubComponentTypeName(subcomponent)>
    protected ${type} ${subcomponent.getName()};
    public ${type} getComponent${subcomponent.getName()?cap_first}() { return this.${subcomponent.getName()}; }<#t>
    <#sep>

    </#sep>
  </#list>
</#macro>