<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/util/Utils.ftl" as Utils>

<#macro printInputClassHeader comp compHelper isTOPClass>
  <#assign className>
      <#if isTOPClass>
        ${comp.getName()}InputTOP
      <#else>
        ${comp.getName()}Input
      </#if>
    </#assign>

  public class ${className} <@Utils.printFormalTypeParameters comp=comp/>
    <#if comp.isPresentParentComponent()> extends <@Utils.componentInputSuperClassFQN comp=comp/>
        <#if comp.getParent().getLoadedSymbol().hasTypeParameter()><<#list compHelper.getSuperCompActualTypeArguments() as scTypeParams>${scTypeParams}<#sep>, </#sep></#list>></#if>
    </#if>
  implements IInput
</#macro>