<#-- (c) https://github.com/MontiCore/monticore -->
<#-- This template creates implementation with exception throwing methods for components that have no implementation
  -- specified.
  -->

<#import "/templates/util/Utils.ftl" as Utils>
<#import "/templates/implementation/InnerImplementationClass.ftl" as InnerImplementation>

<#macro printImplementationClassMethods comp compHelper identifier isTOPClass=false>
  <#assign compName=comp.getName()>
  <#assign className>${compName}Impl<#if isTOPClass>TOP</#if></#assign>
  public ${className}(<@Utils.printConfigurationParametersAsList comp=comp/>) {}

  public <@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/> getInitialValues() {
    return new <@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/>();
  }

  public <@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/> compute(<@Utils.componentInputClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/> ${identifier.getInputName()}) {
    return new <@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/>();
  }

</#macro>