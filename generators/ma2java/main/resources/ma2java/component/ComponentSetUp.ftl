<#-- (c) https://github.com/MontiCore/monticore -->

<@printSetUp ast.getSymbol()/>

<@printSetUpSubcomponents ast.getSymbol()/>

<@printSetPorts ast.getSymbol()/>

<#macro printSetUp comp>
  public void setUp(<@printPortsAsParameters ast.getSymbol()/>) {
  <#if comp.isPresentParentComponent()>
    super.setUp();
  </#if>
  <#if comp.isAtomic()>
    this.setPorts(<#list compHelper.getLocalPortSymbolsSorted(comp) as port>${port.getName()}<#sep>, </#list>);
    this.init();
  <#else>
    this.setUpSubcomponents();
    this.setPorts(<#list compHelper.getLocalPortSymbolsSorted(comp) as port>${port.getName()}<#sep>, </#list>);
    this.init();
  </#if>
  }

  <#if comp.hasPorts()>
    public void setUp() {
      <#list comp.getPorts() as port>
        montiarc.rte.timesync.Port ${port.getName()} = new montiarc.rte.timesync.<#if port.isDelayed()>Delayed<#else>Undelayed</#if>Port<>();
        ${port.getName()}.setName(!this.getInstanceName().isBlank() ? this.getInstanceName() + "." + "${port.getName()}" : "${port.getName()}");
      </#list>
      this.setUp(<#list comp.getPorts() as port>${port.getName()}<#sep>, </#sep></#list>);
    }
  </#if>
</#macro>

<#macro printSetPorts comp>
  // ports
  public void setPorts(<@printPortsAsParameters comp/>) {
    <#list compHelper.getLocalPortSymbolsSorted(comp)>
      <#items as port>
        this.${port.getName()} = ${port.getName()};
      </#items>
      <#else>
    </#list>
    <#if !comp.isAtomic()>
      // subcomponents' ports
      <#list compHelper.getVarsForHiddenChannelsMappedToFullPortType(comp) as varName, type>
        ${type} ${varName} = new ${type}();
      </#list>

      <#list compHelper.transformMapMapToSortedListMap(compHelper.mapSubCompNameToPortVariableMap(comp)) as subCompName, paramsList>
        this.${subCompName}.setPorts(<#list paramsList as param>${param}<#sep>, </#list>);
     </#list>
    </#if>
  }
</#macro>

<#macro printSetUpSubcomponents comp>
  public void setUpSubcomponents() {
    <#list comp.getSubComponents() as subcomponent>
      this.${subcomponent.getName()} = new ${compHelper.getSubComponentTypeName(subcomponent)}(<#list compHelper.getParamValues(subcomponent) as param>${param}<#sep>, </#sep></#list>);
      this.${subcomponent.getName()}.setInstanceName(!this.getInstanceName().isBlank() ? this.getInstanceName() + ".${subcomponent.getName()}" : "${subcomponent.getName()}");
      this.${subcomponent.getName()}.setUpSubcomponents();
    </#list>
  }
</#macro>

<#macro printPortsAsParameters comp>
  <#list compHelper.getLocalPortSymbolsSorted(comp) as port>
    montiarc.rte.timesync.<#if port.isIncoming()>IPortIn<#else>IPortOut</#if><${compHelper.getRealPortTypeString(port)}> ${port.getName()}<#sep>, </#sep>
  </#list>
</#macro>
