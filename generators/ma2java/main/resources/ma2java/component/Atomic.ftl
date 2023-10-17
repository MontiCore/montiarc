<#-- (c) https://github.com/MontiCore/monticore -->

<#if compHelper.getAutomatonBehavior(ast).isPresent()>
  ${tc.includeArgs("ma2java.component.Automaton.ftl", compHelper.getAutomatonBehavior(ast).get(), compHelper.asList(ast.getSymbol()))}
<#elseif compHelper.getComputeBehavior(ast).isPresent()>
  ${tc.includeArgs("ma2java.component.Compute.ftl", compHelper.getComputeBehavior(ast).get(), compHelper.asList(ast.getSymbol()))}
  <#if compHelper.getInitBehavior(ast).isPresent()>
  ${tc.includeArgs("ma2java.component.Init.ftl", compHelper.getInitBehavior(ast).get(), compHelper.asList(ast.getSymbol()))}
  <#else>
    <@printInit />
  </#if>
<#else>
  <@printInit />
  <@printCompute />
</#if>

<@printSetUp ast.getSymbol()/>

<@printTick ast.getSymbol()/>

<#macro printTick comp>
  @Override
  public void tick() {
    // update outgoing ports
    <#if !comp.isEmptyParents()>
      super.tick();
    </#if>
    <#list comp.getOutgoingPorts() as port>
      <#lt> this.${port.getName()}.tick();
    </#list>
  }
</#macro>

<#macro printSetUp comp>
  public void setUp() {
    <#if !comp.isEmptyParents()>
      super.setUp();
    </#if>
    <#list comp.getPorts() as port>
      this.${port.getName()} = new <@printPortType port/>(!this.getInstanceName().isBlank() ? this.getInstanceName() + "." + "${port.getName()}" : "${port.getName()}");
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

<#macro printPortType port>
  montiarc.rte.timesync.
  <#if port.getType().isPrimitive()>
    ${port.getType().print()?cap_first}<#if port.isDelayed()>Delay<#elseif port.isOutgoing()>Out<#else>In</#if>Port
  <#else>
    <#if port.isDelayed()>Delay<#elseif port.isOutgoing()>Out<#else>In</#if>Port<>
  </#if>
</#macro>