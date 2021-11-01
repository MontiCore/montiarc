<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/util/Utils.ftl" as Utils>

<#macro importFromAST comp compHelper>
    <#list compHelper.getImports(comp) as _import>
      import ${_import.getStatement()}<#if _import.isStar()>.*</#if>;
    </#list>
</#macro>

<#macro innerComponentClassesImportsWithSuffix comp compHelper suffix>
    <#list compHelper.getAllInnerComponents(comp) as inner>
      import <@Utils.componentClassFQNWithSuffix comp=comp suffix=suffix/>;
    </#list>
</#macro>

<#macro innerComponentClassesImports comp compHelper>
    <@innerComponentClassesImportsWithSuffix comp=comp compHelper=compHelper suffix=""/>
</#macro>

<#macro printImportsForComponentClass comp compHelper>
    <#assign compFQN = comp.getFullName()>
    <@importFromAST comp=comp compHelper=compHelper/>
    <#if comp.getPackageName() != "">
      import <@Utils.componentInputClassFQN comp=comp/>;
      import <@Utils.componentResultClassFQN comp=comp/>;
      <#if comp.isAtomic()>
        import <@Utils.componentImplClassFQN comp=comp/>;
      </#if>
    </#if>
    <@innerComponentClassesImports comp = comp compHelper=compHelper/>
    <@innerComponentClassesImportsWithSuffix comp=comp compHelper=compHelper suffix="Input"/>
    <@innerComponentClassesImportsWithSuffix comp=comp compHelper=compHelper suffix="Result"/>
    <@innerComponentClassesImportsWithSuffix comp=comp compHelper=compHelper suffix="Impl"/>
  import de.montiarc.runtimes.timesync.delegation.IComponent;
  import de.montiarc.runtimes.timesync.delegation.Port;
  import de.montiarc.runtimes.timesync.implementation.IComputable;
  import de.montiarc.runtimes.Log;
</#macro>

<#macro printImportsForResultClass comp compHelper>
    <@Utils.printImportsFromAST comp=comp helper=compHelper/>
    <@Utils.printImportsForInnerComponents comp=comp/>
  import de.montiarc.runtimes.timesync.implementation.IResult;
</#macro>

<#macro printImportsForInputClass comp compHelper>
    <@Utils.printImportsFromAST comp=comp helper=compHelper/>
    <@Utils.printImportsForInnerComponents comp = comp/>
  import de.montiarc.runtimes.timesync.implementation.IInput;
</#macro>

<#macro printImportsForImplementationClass comp compHelper>
    <@Utils.printImportsFromAST comp=comp helper=compHelper/>
    <@Utils.printImportsForInnerComponents comp = comp/>
  import de.montiarc.runtimes.timesync.implementation.IComputable;
</#macro>