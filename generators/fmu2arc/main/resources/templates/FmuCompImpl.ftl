<#-- (c) https://github.com/MontiCore/monticore -->

${signature("fmu", "fmuFile")}
<#include "/templates/Utils.ftl">
${p}

public class ${fmu.getName()}CompImpl
        extends montiarc.rte.component.AbstractComponent<${syncMsgClass}, ${eventClass}>
        implements
        ${fmu.getName()}Comp,
        ${fmu.getName()}Context {

<#list fixedParamVars as v>
  protected final ${toJavaType(v.getType())} param_${sanitizeName(v.getName())};

  @Override
  public ${toJavaType(v.getType())} param_${sanitizeName(v.getName())}() {
    return this.param_${sanitizeName(v.getName())};
  }
</#list>


<#list inputVars as v>
  <#assign boxedType = "java.lang." + toBoxedJavaType(v.getType())>
  @Override
  public montiarc.rte.port.InPort<${boxedType}> port_${sanitizeName(sanitizeName(v.getName()))}() {
  return this.port_${sanitizeName(v.getName())};
  }
</#list>

<#list outputVars as v>
  <#assign boxedType = "java.lang." + toBoxedJavaType(v.getType())>
  @Override
  public montiarc.rte.port.OutPort<${boxedType}> port_${sanitizeName(v.getName())}() {
  return this.port_${sanitizeName(v.getName())};
  }
</#list>


<#list InOutVars as v>
  <#assign boxedType = "java.lang." + toBoxedJavaType(v.getType())>
  protected montiarc.rte.port.InOutPort<${boxedType}, ${boxedType}> port_${sanitizeName(v.getName())} = null;
</#list>

  protected void setupPorts() {
  <#list inputVars as v>
    this.port_${sanitizeName(v.getName())} = new montiarc.rte.port.ScheduledPort<>(getName() + ".${sanitizeName(v.getName())}", this, scheduler);
  </#list>
  <#list outputVars as v>
    this.port_${sanitizeName(v.getName())} = new montiarc.rte.port.PortForward<>(getName() + ".${sanitizeName(v.getName())}", this);
  </#list>
  }

  protected void setupUnusedOutPorts() {
  this.unconnectedOutputs = java.util.Set.of(
  <#list outputVars as v>
      this.port_${sanitizeName(v.getName())}()<#if v_has_next>,</#if>
  </#list>  );
  }
  <#assign ListType = "<montiarc.rte.port.InOutPort<?, ?>>">
  @Override
  public java.util.List${ListType} getAllInPorts() {
    return java.util.List.of(
  <#list inputVars as v>
      this.port_${sanitizeName(v.getName())}<#if v_has_next>,</#if>
  </#list>);
  }
  <#assign ListType2 = "<montiarc.rte.port.OutPort<?>>">
  @Override
  public java.util.List${ListType2} getAllOutPorts() {
    return java.util.List.of(
  <#list outputVars as v>
        this.port_${sanitizeName(v.getName())}()<#if v_has_next>,</#if>
  </#list>);
  }

  @Override
  public java.util.List${ListType} getAllSyncedInPorts() {
    return java.util.List.of(
            <#list syncInputVars as v>this.port_${sanitizeName(v.getName())}<#if v_has_next>, </#if></#list>);
  }

  public java.util.List${ListType} getAllMsgEventInPorts() {
    return java.util.List.of(
            <#list tunParamVars as v>this.port_${sanitizeName(v.getName())}<#if v_has_next>, </#if></#list>);
  }

  protected ${fmu.getName()}CompImpl(
      String name,
      montiarc.rte.scheduling.Scheduler scheduler,
      montiarc.rte.oracle.OracleFactory oracleFactory<#list fixedParamVars as v>
        , ${toJavaType(v.getType())} param_${sanitizeName(v.getName())}
      </#list>) {
    super(name, scheduler);
  <#list fixedParamVars as v>
    this.param_${sanitizeName(v.getName())} = param_${sanitizeName(v.getName())};
  </#list>
    <#list fixedParamVars as v>
    final ${toJavaType(v.getType())} ${sanitizeName(v.getName())} = this.param_${sanitizeName(v.getName())};
    </#list>
    this.isAtomic = true;
    setupPorts();
    setupBehavior();
    setupUnusedOutPorts();
    this.scheduler.register(this);
    this.oracle = oracleFactory.createOracleFor(this.getName());
    de.se_rwth.commons.logging.Log.info(
        () -> "pkg.${fmu.getName()} with"
          + " scheduler type = " + this.scheduler.getClass().getSimpleName() + ";"
          + " oracle type = " + this.oracle.getClass().getSimpleName() + ";",
        this.getName() + "#create_comp");
  }

  @Override
  protected void handleMessageWithBehavior(montiarc.rte.port.InPort<?> p) {
  <#list inputVars as v>
    if (p == port_${sanitizeName(v.getName())}) {
      this.getBehavior().msg_${sanitizeName(v.getName())}(portValueOf_${sanitizeName(v.getName())}());;
    }
  </#list>
  }

  @Override
  protected ${syncMsgClass} buildSyncMessage() {
    return doBuildSyncMessage();
  }

  protected ${syncMsgClass} doBuildSyncMessage() {
    return new ${syncMsgClass}(
            <#list inputVars as v> portValueOf_${sanitizeName(v.getName())}()<#if v_has_next>, </#if></#list>
    );
  }

  @Override
  protected Object portValueOf(montiarc.rte.port.InPort<?> p) {
    <#list inputVars as v>
      if (p == this.port_${sanitizeName(v.getName())}) {
        return portValueOf_${sanitizeName(v.getName())}();
      }<#if v_has_next> else</#if>
    </#list> <#if inputVars?has_content> else
      throw new IllegalArgumentException(
          "Port " + p.getQualifiedName() + " is not available in component " + getName());
  <#else>
    throw new IllegalArgumentException(
            "Port " + p.getQualifiedName() + " is not available in component " + getName());
  </#if>
  }

  <#list inputVars as v>
  protected ${toJavaType(v.getType())} portValueOf_${sanitizeName(v.getName())}() {
    return this.port_${sanitizeName(v.getName())}.isTickBlocked() ? ${getJavaDefault(v.getType())} : this.port_${sanitizeName(v.getName())}().peekBuffer().getData();
  }
  </#list>

  protected void setupBehavior() {
    this.behavior = new ${fmu.getName()}Compute(this, this.getName());
    }

  @Override
  public java.util.List<montiarc.rte.component.SimComponent> getAllSubcomponents() {
    final java.util.ArrayList<montiarc.rte.component.SimComponent> allSubcomponentList = new java.util.ArrayList<>();
    return allSubcomponentList;
  }
}
