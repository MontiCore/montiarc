<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
public void init() {
  <#if ast.getSymbol().isAtomic()>
      <@MethodNames.getBehavior/>().init();
  <#else>
      <#list ast.getSubComponents() as sub>${prefixes.subcomp()}${sub.getName()}().init();</#list>
  </#if>
}
