<#-- (c) https://github.com/MontiCore/monticore -->
<#-- This template creates implementation with exception throwing methods for components that have no implementation
  -- specified.
  -->

<#import "/templates/util/Utils.ftl" as Utils>
<#import "/templates/implementation/InnerImplementationClass.ftl" as InnerImplementation>

<#macro printImplementationClassMethods comp compHelper identifier isTOPClass=false>
  <#assign compName=comp.getName()>
  <#assign className>${compName}Impl<#if isTOPClass>TOP</#if></#assign>
  public ${className}(<@Utils.printConfigurationParametersAsList comp=comp/>) {
    <#if !isTOPClass>
      throw new java.lang.UnsupportedOperationException("Invoking constructor on abstract implementation <@Utils.componentClassFQN comp=comp/>Impl");
    </#if>
  }

  public <@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/> getInitialValues() {
  throw new java.lang.UnsupportedOperationException("Invoking getInitialValues() on abstract implementation <@Utils.componentClassFQN comp=comp/>Impl");
  }

  public <@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/> compute(<@Utils.componentInputClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/> ${identifier.getInputName()}) {
  throw new java.lang.UnsupportedOperationException("Invoking compute() on abstract implementation <@Utils.componentClassFQN comp=comp/>Impl");
  }

</#macro>