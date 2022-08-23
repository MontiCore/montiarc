<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/util/Utils.ftl" as Utils>
<#import "/templates/component/ComponentBody.ftl" as ComponentBody>

<#macro printInnerComponentClass innerComp compHelper identifier isTOPClass>
    <@printInnerComponentHeader innerComp=innerComp compHelper=compHelper isTOPClass=isTOPClass/> {
    <@ComponentBody.printComponentClassBody comp=innerComp compHelper=compHelper identifier=identifier isTOPClass=isTOPClass/>
  }
</#macro>

<#macro printInnerComponentHeader innerComp compHelper isTOPClass>
  <#assign compName>
    <#if isTOPClass>
      ${innerComp.getName()}TOP
    <#else>
      ${innerComp.getName()}
    </#if>
  </#assign>

  public static class ${compName} <@Utils.printFormalTypeParameters comp=innerComp/>
    <#if innerComp.isPresentParentComponent()> extends <@Utils.componentSuperClassFQN comp=innerComp/>
        <#if innerComp.getParent().getTypeInfo().hasTypeParameter()>
            <<#list compHelper.getSuperCompActualTypeArguments() as scTypeParams>${scTypeParams}<#t><#sep>, </#sep></#list>>
        </#if>
    </#if>
  implements IComponent
</#macro>

