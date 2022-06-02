<#-- (c) https://github.com/MontiCore/monticore -->
<#-- This template checks if a component has a behavior implementation, and if it has one: which one it has. Then,
  -- depending on the case, it delegates to different templates that provide the code generation for the respective
  -- case.
  --
  -- In addition, it calls the template that creates the implementations of inner components.
  -->
<#import "/templates/util/Utils.ftl" as Utils>
<#import "/templates/implementation/ImplementationAbstractImpl.ftl" as AbstractImpl>
<#import "/templates/implementation/ImplementationAutomatonImpl.ftl" as AutomatonImpl>
<#import "/templates/implementation/ImplementationComputeImpl.ftl" as ComputeImpl>
<#import "/templates/implementation/InnerImplementationClass.ftl" as InnerImplementation>

<#macro printImplementationClassBody comp compHelper identifier isTOPClass=false>
  <#if compHelper.hasAutomatonBehavior()>
    <@AutomatonImpl.printImplementationClassMethods comp=comp compHelper=compHelper identifier=identifier isTOPClass=isTOPClass/>
  <#elseif compHelper.hasComputeBehavior()>
    <@ComputeImpl.printImplementationClassMethods comp=comp compHelper=compHelper identifier=identifier isTOPClass=isTOPClass/>
  <#else>
    <@AbstractImpl.printImplementationClassMethods comp=comp compHelper=compHelper identifier=identifier isTOPClass=isTOPClass/>
  </#if>

  <#list comp.getInnerComponents() as innerComp>
    <#assign innerIdentifier=identifier.getNewIdentifier(innerComp)>
    <@InnerImplementation.printInnerImplementationClass innerComp=innerComp compHelper=compHelper identifier=innerIdentifier isTOPClass=isTOPClass/>
  </#list>
</#macro>