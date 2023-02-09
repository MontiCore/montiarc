<#-- (c) https://github.com/MontiCore/monticore -->

// subcomponents
<@printSubcomponents ast.getSymbol()/>

<@printGetAllSubcomponents ast.getSymbol()/>

<@printCompute/>

<@printSetUp ast.getSymbol()/>

<@printInit ast.getSymbol()/>

<@printTick ast.getSymbol()/>

<#macro printSubcomponents comp>
  <#assign subComponents = comp.getSubComponents()>
  <#list subComponents as subcomponent>
    <#assign type = compHelper.getSubComponentTypeName(subcomponent)>
    protected ${type} ${subcomponent.getName()};
    public ${type} getComponent${subcomponent.getName()?cap_first}() { return this.${subcomponent.getName()}; }<#t>
    <#sep>

    </#sep>
  </#list>
</#macro>

<#macro printGetAllSubcomponents comp>
  protected java.util.List${r"<montiarc.rte.timesync.IComponent>"} getAllSubcomponents() {
    return java.util.Arrays.asList(new montiarc.rte.timesync.IComponent[] {
    <#list comp.getSubComponents() as subcomponent>
      ${subcomponent.getName()}<#sep>, </#sep>
    </#list>
    });
  }
</#macro>

<#macro printCompute>
  @Override
  public void compute() {
    java.util.List${r"<montiarc.rte.timesync.IComponent>"} notYetComputed = new java.util.ArrayList<>(getAllSubcomponents());
    while(notYetComputed.size() > 0) {
      java.util.Set${r"<montiarc.rte.timesync.IComponent>"} computedThisIteration = new java.util.HashSet<>();
      for(montiarc.rte.timesync.IComponent subcomponent : notYetComputed) {
        if(subcomponent.isSynced()) {
          subcomponent.compute();
          computedThisIteration.add(subcomponent);
        }
      }
      if(computedThisIteration.isEmpty()) {
        throw new RuntimeException("Could not complete compute cycle due to not all ports being synced. Likely reasons: Forgot to call init() or cyclic connector loop.");
      } else {
        notYetComputed.removeAll(computedThisIteration);
      }
    }
  }
</#macro>

<#macro printSetUp comp>
  public void setUp() {
    <#if comp.isPresentParent()>
      super.setUp();
    </#if>
    <#list comp.getSubComponents() as subcomponent>
      this.${subcomponent.getName()} = new ${compHelper.getSubComponentTypeName(subcomponent)}(<#list compHelper.getParamValues(subcomponent) as param>${param}<#sep>, </#sep></#list>);
      this.${subcomponent.getName()}.setInstanceName(!this.getInstanceName().isBlank() ? this.getInstanceName() + ".${subcomponent.getName()}" : "${subcomponent.getName()}");
      this.${subcomponent.getName()}.setUp();
    </#list>
    <#list comp.getAstNode().getConnectors() as connector>
      <#assign source = connector.getSource()>
      <#list connector.getTargetList() as target>
        <#if !source.isPresentComponent() && target.isPresentComponent()>
          this.${source.getQName()} = ${target.getComponent()}.get${target.getPort()?cap_first}();
        <#elseif source.isPresentComponent() && !target.isPresentComponent()>
          this.${target.getQName()} = ${source.getComponent()}.get${source.getPort()?cap_first}();
        <#elseif source.isPresentComponent() && target.isPresentComponent()>
          ${source.getComponent()}.get${source.getPort()?cap_first}().connect(${target.getComponent()}.get${target.getPort()?cap_first}());
        <#else>
        </#if>
      </#list>
    </#list>
  }
</#macro>

<#macro printInit comp>
  @Override
  public void init() {
    <#list comp.getSubComponents() as subcomponent>
      this.${subcomponent.getName()}.init();
    </#list>
  }
</#macro>

<#macro printTick comp>
  @Override
  public void tick() {
    // update subcomponents
    <#list comp.getSubComponents() as subcomponent>
      <#lt>this.${subcomponent.getName()}.tick();
    </#list>
  }
</#macro>
