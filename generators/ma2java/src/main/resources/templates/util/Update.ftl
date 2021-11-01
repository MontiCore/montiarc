<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Prints the update() method for both atomic and composed components. -->
<#macro printUpdateMethod comp>
  @Override
  public void update() {
    <#if comp.isPresentParentComponent()>
    <#lt>    super.update();

    </#if>
    <#if comp.isDecomposed()>
      <#lt>    // update subcomponent instances
      <#assign subComponents = comp.getSubComponents()>
      <#list subComponents as subcomponent>
        <#lt>    this.${subcomponent.getName()}.update();
      </#list>
    </#if>
    <#t>
    <#if !comp.isDecomposed()>
      <#lt>    // update computed value for next computation cycle in all outgoing ports
        <#assign portsOut = comp.getOutgoingPorts()>
        <#list portsOut as port>
          <#lt>    this.${port.getName()}.update();
        </#list>
    </#if>
  }
</#macro>