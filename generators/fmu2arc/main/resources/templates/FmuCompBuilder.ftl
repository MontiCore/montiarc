<#-- (c) https://github.com/MontiCore/monticore -->

${signature("fmu")}
<#include "/templates/Utils.ftl">

${p}

public class ${fmu.getName()}CompBuilder {
  protected String name;

  public ${fmu.getName()}CompBuilder setName(String name) {
    this.name = name;
    return this;
  }

  public String getName() {
    return this.name;
  }

  protected montiarc.rte.component.SimComponent superComponent;

  public ${fmu.getName()}CompBuilder setSuperComponent(montiarc.rte.component.SimComponent superComponent) {
    this.superComponent = superComponent;
    return this;
  }

  public montiarc.rte.component.SimComponent getSuperComponent() {
    return this.superComponent;
  }

  protected java.util.Optional<montiarc.rte.scheduling.Scheduler> scheduler = java.util.Optional.empty();

  public java.util.Optional<montiarc.rte.scheduling.Scheduler> getScheduler() {
    return this.scheduler;
  }

  public ${fmu.getName()}CompBuilder setScheduler(java.util.Optional<montiarc.rte.scheduling.Scheduler> scheduler) {
    this.scheduler = scheduler;
    return this;
  }

  public ${fmu.getName()}CompBuilder setScheduler(montiarc.rte.scheduling.Scheduler scheduler) {
    this.scheduler = java.util.Optional.of(scheduler);
    return this;
  }

  protected montiarc.rte.oracle.OracleFactory oracleFactory = montiarc.rte.oracle.OracleFactory
          .withDefaultStrategy(montiarc.rte.oracle.OracleFactory.preferFirst());

  public ${fmu.getName()}CompBuilder setOracleFactory(montiarc.rte.oracle.OracleFactory oracleFactory) {
    this.oracleFactory = oracleFactory;
    return this;
  }

  public montiarc.rte.oracle.OracleFactory getOracleFactory() {
    return this.oracleFactory;
  }

  public ${fmu.getName()}CompBuilder(String name) {
    this.name = name;
  }

  public ${fmu.getName()}CompBuilder() {
    this("");
  }

<#list fixedParamVars as v >
  protected ${toJavaType(v.getType())} param_${sanitizeName(v.getName())} = ${getJavaDefault(v.getType())};

  public ${fmu.getName()}CompBuilder set_param_${sanitizeName(v.getName())}(${toJavaType(v.getType())} param_${sanitizeName(v.getName())}) {
    this.param_${sanitizeName(v.getName())} = param_${sanitizeName(v.getName())};
    return this;
  }

  public ${toJavaType(v.getType())} get_param_${sanitizeName(v.getName())}(){
    return this.param_${sanitizeName(v.getName())};
  }
</#list>

  public boolean isValid() {
    return !(name == null || "".equals(name) || oracleFactory == null);
  }

  public ${fmu.getName()}Comp build() {
    assert isValid() : "Illegal builder configuration for component "
            + ((name == null || name.isBlank()) ? "ERR: no name given" : name);
    ${fmu.getName()}CompImpl component = new ${fmu.getName()}CompImpl(
            getName(),
            getScheduler().orElse(new montiarc.rte.scheduling.CoordinatingScheduler(getOracleFactory())),
            getOracleFactory()<#list fixedParamVars as v>
        , get_param_${sanitizeName(v.getName())}()
    </#list>);
    component.setSuperComponent(superComponent);
    return component;
  }
}
