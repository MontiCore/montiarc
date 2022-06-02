<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/util/Utils.ftl" as Utils>
<#import "/templates/input/InputBody.ftl" as InputBody>

<#macro printInnerInputClass innerComp compHelper isTOPClass>
    <@printInnerInputHeader innerComp=innerComp compHelper=compHelper isTOPClass=isTOPClass/> {
    <@InputBody.printInputClassBody comp=innerComp compHelper=compHelper isTOPClass=isTOPClass/>
  }
</#macro>

<#macro printInnerInputHeader innerComp compHelper isTOPClass>
  <#assign className>
    <#if isTOPClass>
      ${innerComp.getName()}InputTOP
    <#else>
      ${innerComp.getName()}Input
    </#if>
  </#assign>

  public static class ${className} <@Utils.printFormalTypeParameters comp=innerComp/>
    <#if innerComp.isPresentParentComponent()> extends <@Utils.componentInputSuperClassFQN comp=innerComp/>
        <#if innerComp.getParent().getLoadedSymbol().hasTypeParameter()><<#list compHelper.getSuperCompActualTypeArguments() as scTypeParams>${scTypeParams}<#sep>, </#sep></#list>></#if>
    </#if>
  implements IInput
</#macro>