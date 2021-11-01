<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/util/Utils.ftl" as Utils>
<#import "/templates/result/ResultBody.ftl" as ResultBody>

<#macro printInnerResultClass innerComp compHelper isTOPClass>
    <@printInnerResultHeader innerComp=innerComp compHelper=compHelper isTOPClass=isTOPClass/> {
    <@ResultBody.printResultClassBody comp = innerComp compHelper=compHelper isTOPClass=isTOPClass/>
  }
</#macro>

<#macro printInnerResultHeader innerComp compHelper isTOPClass>
  <#assign className>
    <#if isTOPClass>
      ${innerComp.getName()}ResultTOP
    <#else>
      ${innerComp.getName()}Result
    </#if>
  </#assign>

  public static class ${className} <@Utils.printFormalTypeParameters comp=innerComp/>
    <#if innerComp.isPresentParentComponent()> extends <@Utils.componentResultSuperClassFQN comp=innerComp/>
        <#if innerComp.getParent().getLoadedSymbol().hasTypeParameter()><<#list compHelper.getSuperCompActualTypeArguments() as scTypeParams>${scTypeParams}<#sep>, </#sep></#list>></#if>
    </#if>
  implements IResult
</#macro>