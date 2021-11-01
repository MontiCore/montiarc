<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/util/Utils.ftl" as Utils>

<#macro printResultClassHeader comp compHelper isTOPClass>
  public class ${comp.getName()}Result<#if isTOPClass>TOP</#if><@Utils.printFormalTypeParameters comp=comp/>
    <#if comp.isPresentParentComponent()> extends <@Utils.componentResultSuperClassFQN comp=comp/>
        <#if comp.getParent().getLoadedSymbol().hasTypeParameter()><<#list compHelper.getSuperCompActualTypeArguments() as scTypeParams>${scTypeParams}<#sep>, </#sep></#list>></#if>
    </#if>
  implements IResult
</#macro>