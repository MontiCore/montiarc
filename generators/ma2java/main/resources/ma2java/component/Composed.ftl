<#-- (c) https://github.com/MontiCore/monticore -->

// subcomponents
<@printSubcomponents ast.getSymbol()/>

<@printGetAllSubcomponents ast.getSymbol()/>

<@printCompute/>

<@printInit ast.getSymbol()/>

<@printUpdate ast.getSymbol()/>

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
        if(subcomponent.allInputsSynced()) {
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
    // log port values
    this.logPortValues();
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

<#macro printUpdate comp>
  @Override
  public void update() {
    // update subcomponents
    <#list comp.getSubComponents() as subcomponent>
      <#lt>  this.${subcomponent.getName()}.update();
    </#list>
  }
</#macro>
