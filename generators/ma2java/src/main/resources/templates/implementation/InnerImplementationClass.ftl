<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/util/Utils.ftl" as Utils>
<#import "/templates/implementation/ImplementationBody.ftl" as ImplementationBody>

<#macro printInnerImplementationClass innerComp compHelper identifier isTOPClass>
    <@printInnerImplementationHeader innerComp=innerComp compHelper=compHelper isTOPClass=isTOPClass/> {
    <@ImplementationBody.printImplementationClassBody comp=innerComp compHelper=compHelper identifier=identifier isTOPClass=isTOPClass/>
  }
</#macro>

<#macro printInnerImplementationHeader innerComp compHelper isTOPClass>
  <#assign className>
    <#if isTOPClass>
      ${innerComp.getName()}ImplTOP
    <#else>
      ${innerComp.getName()}Impl
    </#if>
  </#assign>

  public static class ${className} <@Utils.printFormalTypeParameters comp=innerComp/>
  implements
    IComputable<<@Utils.componentInputClassFQN comp=innerComp/> <@Utils.printFormalTypeParameters comp=innerComp/>,
    <@Utils.componentResultClassFQN comp=innerComp/> <@Utils.printFormalTypeParameters comp=innerComp/>>
</#macro>