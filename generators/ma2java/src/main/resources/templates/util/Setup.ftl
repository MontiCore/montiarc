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

    // set up output ports
    <#assign portsOut = comp.getOutgoingPorts()>
    <#list portsOut as port>
      <#lt>    this.${port.getName()} = new Port<${compHelper.getRealPortTypeString(port)}>();
    </#list>
    <#t>
    <#if !comp.isAtomic()>
      <#lt>    // propagate children's output ports to own output ports
        <#assign connectors = comp.getAstNode().getConnectors()>
        <#list connectors as connector>
            <#list connector.getTargetList() as target>
                <#if !compHelper.isIncomingPort(comp, connector.getSource(), target, false)>
                    <#assign targetComponentName = compHelper.getConnectorComponentName(connector.getSource(), target, false)/>
                    <#assign targetPortName = compHelper.getConnectorPortName(connector.getSource(), target, false)?cap_first/>
                    <#assign sourceComponentName = compHelper.getConnectorComponentName(connector.getSource(), target,true)/>
                    <#assign sourcePortName = compHelper.getConnectorPortName(connector.getSource(), target, true)?cap_first/>
                    <#lt>    ${targetComponentName}.setPort${targetPortName}(${sourceComponentName}.getPort${sourcePortName}());
                </#if>
            </#list>
        </#list>
    </#if>

    // set up input ports
    <#assign portsIn = comp.getIncomingPorts()>
    <#list portsIn as port>
      <#lt>    this.${port.getName()} = new Port<${compHelper.getRealPortTypeString(port)}>();
    </#list>
    <#t>
    <#if !comp.isAtomic()>
      <#lt>    // connect outputs of children with inputs of children, by giving
      <#lt>    // the inputs a reference to the sending ports
        <#assign connectors = comp.getAstNode().getConnectors()>
        <#list connectors as connector>
            <#list connector.getTargetList() as target>
                <#if compHelper.isIncomingPort(comp, connector.getSource(), target, false)>
                    <#assign targetComponentName = compHelper.getConnectorComponentName(connector.getSource(), target, false)/>
                    <#assign targetPortName = compHelper.getConnectorPortName(connector.getSource(), target, false)?cap_first/>
                    <#assign sourceComponentName = compHelper.getConnectorComponentName(connector.getSource(), target,true)/>
                    <#assign sourcePortName = compHelper.getConnectorPortName(connector.getSource(), target, true)?cap_first/>
                    <#lt>    ${targetComponentName}.setPort${targetPortName}(${sourceComponentName}.getPort${sourcePortName}());
                </#if>
            </#list>
        </#list>
    </#if>

    <#if comp.isAtomic()>
      <#lt>    this.initialize();
    </#if>
  }
</#macro>