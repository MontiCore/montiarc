<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/util/Utils.ftl" as Utils>

<#macro printImplementationClassHeader comp compHelper isTOPClass>
    <#assign compName = comp.getName()>
    <#assign headerStart>
        <#if isTOPClass>
            public abstract class ${compName}ImplTOP
        <#else>
            public class ${compName}Impl
        </#if>
    </#assign>
  ${headerStart}<@Utils.printFormalTypeParameters comp=comp/>
  implements IComputable<<@Utils.componentInputClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/>, <@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/>>
</#macro>