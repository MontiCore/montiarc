<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Prints the init() method for both atomic and composed components. -->
<#macro printInitMethod comp compHelper>
  @Override
  public void init() {
    <#if comp.isPresentParentComponent()>
      <#lt>    super.init();

    </#if>
    // set up unused input ports
    <#assign portsIn = comp.getIncomingPorts()>
    <#list portsIn as port>
      <#lt>    if (this.${port.getName()} == null) { this.${port.getName()} = Port.EMPTY; }
    </#list>

    <#if !comp.isAtomic()>
      <#lt>    // init all subcomponents
        <#assign subComponents = comp.getSubComponents()>
        <#list subComponents as subcomponent>
          <#lt>    this.${subcomponent.getName()}.init();
        </#list>
    </#if>
  }
</#macro>