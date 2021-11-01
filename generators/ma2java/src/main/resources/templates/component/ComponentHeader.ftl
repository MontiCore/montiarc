<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/util/Utils.ftl" as Utils>

<#macro printHeaderStart compName isTOPClass>
  <#if isTOPClass>
    public abstract class ${compName}TOP<#t>
  <#else>
    public class ${compName}<#t>
  </#if>
</#macro>

<#macro printParentExtensionIfPresent childComp compHelper>
  <#if comp.isPresentParentComponent()> extends <@Utils.componentSuperClassFQN comp=comp/>
    <#if comp.getParent().getLoadedSymbol().hasTypeParameter()>
      <<#list compHelper.getSuperCompActualTypeArguments() as scTypeParams>
        ${scTypeParams} <#sep>, </#sep>
      </#list>>
    </#if>
  </#if>
</#macro>

<#macro printComponentClassHeader comp compHelper isTOPClass>
  <#assign compName = comp.getName()>
  <#assign headerStart><@printHeaderStart compName=compName isTOPClass=isTOPClass/></#assign>
  <#assign typeParams><@Utils.printFormalTypeParameters comp=comp/></#assign>
${headerStart}${typeParams}
  <@printParentExtensionIfPresent childComp=comp compHelper=compHelper/>
  implements IComponent
</#macro>

