<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Prints the attributes and getter and setter for ports. -->
<#macro printPortsWithGetterAndSetter comp compHelper>
  <#assign portsIn = comp.getIncomingPorts()>
  <#assign portsOut = comp.getOutgoingPorts()>

  <#list portsIn>
    // incoming ports
    <#items as port>
      <#assign type = compHelper.getRealPortTypeString(port.getComponent().get(), port)>
      protected Port<${type}> ${port.getName()};
    </#items>
  </#list>

  <#list portsOut>
    // outgoing ports
    <#items as port>
      <#assign type = compHelper.getRealPortTypeString(port.getComponent().get(), port)>
      protected Port<${type}> ${port.getName()};
    </#items>
  </#list>

  <#list portsIn>
    // getters for incoming ports
    <#items as port>
      <#assign type = compHelper.getRealPortTypeString(port.getComponent().get(), port)>
      public Port<${type}> getPort${port.getName()?cap_first}() {
        return this.${port.getName()};
      }
    </#items>
  </#list>

  <#list portsOut>
    // getters for outgoing ports
    <#items as port>
      <#assign type = compHelper.getRealPortTypeString(port.getComponent().get(), port)>
      public Port<${type}> getPort${port.getName()?cap_first}() {
        return this.${port.getName()};
      }
    </#items>
  </#list>

  <#list portsIn>
    // setters for incoming ports
    <#items as port>
      <#assign type = compHelper.getRealPortTypeString(port.getComponent().get(), port)>
      public void setPort${port.getName()?cap_first}(Port<${type}> ${port.getName()}) {
        this.${port.getName()} = ${port.getName()};
      }
    </#items>
  </#list>

  <#list portsOut>
    // setters for outgoing ports
    <#items as port>
      <#assign type = compHelper.getRealPortTypeString(port.getComponent().get(), port)>
      public void setPort${port.getName()?cap_first}(Port<${type}> ${port.getName()}) {
        this.${port.getName()} = ${port.getName()};
      }
    </#items>
  </#list>

</#macro>