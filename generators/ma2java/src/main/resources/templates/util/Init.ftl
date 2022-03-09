<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Prints the init() method for both atomic and composed components. -->
<#macro printInitMethod comp compHelper>
  @Override
  public void init() {
    <#if comp.isPresentParentComponent()>
      super.init();
    </#if>
    <#if !comp.isAtomic()>
      // init all subcomponents
      <#list comp.getSubComponents() as subcomponent>
        this.${subcomponent.getName()}.init();
      </#list>
    </#if>
  }
</#macro>