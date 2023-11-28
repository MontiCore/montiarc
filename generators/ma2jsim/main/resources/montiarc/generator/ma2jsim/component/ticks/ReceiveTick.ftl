<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#assign isAtomic = ast.getSymbol().isAtomic()/>
<#assign hasModeAutomaton = helper.getModeAutomaton(ast).isPresent()/>

public void <@MethodNames.receiveTick/>() {
  <#if hasModeAutomaton>
      <@MethodNames.getModeAutomaton/>().tick();
  </#if>
  <#if isAtomic>
      <@MethodNames.getBehavior/>().tick();
  <#else>
      <#list helper.getSubcomponentsWithoutInPorts(ast) as sub>
          ${prefixes.subcomp()}${sub.getName()}().<@MethodNames.receiveTick/>();
      </#list>
  </#if>
}