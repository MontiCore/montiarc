<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Prints the setup() method for both atomic and composed components. -->
<#macro printSetupMethod comp compHelper>
  @Override
  public void setUp() {
  <#if comp.isPresentParentComponent()>
    <#lt>    super.setUp();
  </#if>
  <#t>
  <#if !comp.isAtomic()>
      <#assign subComponents = comp.getSubComponents()>
      <#list subComponents as subcomponent>
        <#lt>    this.${subcomponent.getName()} = new ${compHelper.getSubComponentTypeName(subcomponent)}(<#list compHelper.getParamValues(subcomponent) as param>${param}<#sep>, </#sep></#list>);
        <#lt>    this.${subcomponent.getName()}.setUp();
      </#list>
  </#if>

    // incoming ports setup
    <#assign portsIn = comp.getIncomingPorts()>
    <#list portsIn as port>
        <#lt>    this.${port.getName()} = new Port<${compHelper.getRealPortTypeString(port)}>();
    </#list>

    // outgoing ports setup
    <#assign portsOut = comp.getOutgoingPorts()>
    <#list portsOut as port>
        <#lt>    this.${port.getName()} = new Port<${compHelper.getRealPortTypeString(port)}>();
    </#list>

    // set up connectors
    <#list comp.getAstNode().getConnectors() as connector>
        <#list compHelper.getConnectorSetupCalls(connector) as call>
        <#lt>    ${call}
        </#list>
    </#list>

    <#if comp.isAtomic()>
      <#lt>    this.initialize();
    </#if>
  }
</#macro>