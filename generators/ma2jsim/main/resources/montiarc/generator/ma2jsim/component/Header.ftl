<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
public <#if isTop>abstract</#if> class ${ast.getName()}${suffixes.component()}<#if isTop>${suffixes.top()}</#if> implements <#-- TODO: type variables -->
  ${ast.getName()}${suffixes.context()},
  <#assign inputTimed = helper.isComponentInputTimeAware(ast)/>
  <#assign outputTimed = helper.isComponentOutputTimeAware(ast)/>
  <#if inputTimed && outputTimed>
    montiarc.rte.component.ITimedComponent
  <#elseif !inputTimed && !outputTimed>
    montiarc.rte.component.IUntimedComponent
  <#else>
    montiarc.rte.component.IComponent${"<"}
      montiarc.rte.port.<#if inputTimed>ITimeAware<#else>TimeUnaware</#if>InPort<?>,
      montiarc.rte.port.Time<#if outputTimed>Aware<#else>Unaware</#if>OutPort<?>
    ${">"}
  </#if>