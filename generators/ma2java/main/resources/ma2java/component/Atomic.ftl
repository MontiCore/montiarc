<#-- (c) https://github.com/MontiCore/monticore -->

<#if compHelper.getAutomatonBehavior(ast).isPresent()>
  ${tc.includeArgs("ma2java.component.Automaton.ftl", compHelper.getAutomatonBehavior(ast).get(), compHelper.asList(ast.getSymbol()))}
<#elseif compHelper.getComputeBehavior(ast).isPresent()>
  ${tc.includeArgs("ma2java.component.Compute.ftl", compHelper.getComputeBehavior(ast).get(), compHelper.asList(ast.getSymbol()))}
<#else>
  <@printInit />
  <@printCompute />
</#if>

<@printUpdate ast.getSymbol()/>

<#macro printUpdate comp>
  @Override
  public void update() {
    // update outgoing ports
    <#list comp.getOutgoingPorts() as port>
      <#lt> this.${port.getName()}.update();
    </#list>
  }
</#macro>

<#macro printInit>
  @Override
  public void init() { }
</#macro>

<#macro printCompute>
  public void compute() { }
</#macro>