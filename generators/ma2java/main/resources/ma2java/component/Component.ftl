<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("isTop")}

${tc.includeArgs("ma2java.component.Header.ftl", ast, compHelper.asList(isTop))} {

  <@printInstanceName/>

  // ports
  ${tc.include("ma2java.component.Port.ftl", ast.getPorts())}

  // parameters
  <@printParameters ast.getSymbol()/>

  // variables
  <@printVariables ast.getSymbol()/>

  <@printConstructor ast.getSymbol() isTop/>

  <#if ast.getSymbol().isDecomposed()>
    ${tc.include("ma2java.component.Composed.ftl", ast)}
  <#else>
    ${tc.include("ma2java.component.Atomic.ftl", ast)}
  </#if>

  <@printSynchronized ast.getSymbol()/>

  <#list ast.getInnerComponents() as innerComp>
    ${tc.includeArgs("ma2java.component.Component.ftl", innerComp, compHelper.asList(isTop))}
  </#list>
}

<#macro printInstanceName>
  private String instanceName = "";

  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }

  public String getInstanceName() {
    return this.instanceName;
  }
</#macro>

<#macro printParameters comp>
  <#list comp.getParameters() as param>
    protected final <@getTypeString param.getType()/> ${param.getName()};
  </#list>
</#macro>

<#macro printVariables comp>
  <#list compHelper.getComponentVariables(comp) as variable>
    protected <@getTypeString variable.getType()/> ${variable.getName()};
  </#list>
</#macro>

<#macro printSynchronized comp>
  @Override
  public boolean isSynced() {
    return
    <#list comp.getAllIncomingPorts() as inPort>
      this.get${inPort.getName()?cap_first}().isSynced()<#sep> && </#sep>
    <#else>
      true
    </#list>;
  }
</#macro>

<#macro printConstructor comp isTop>
  <#assign name>${comp.getName()}<#if isTop>TOP</#if></#assign>
  public ${name}(<@printParametersAsList comp/>) {
    <#if !comp.isEmptySuperComponents()>
      super(<#list comp.getParentConfiguration(comp.getSuperComponents(0)) as parentConfiguration>${compHelper.printExpression(parentConfiguration.getExpression())}<#sep>, </#sep></#list>);
    </#if>

    <#list comp.getParameters() as param>
      this.${param.getName()} = ${param.getName()};
    </#list>

    <#list compHelper.getComponentVariables(comp) as variable>
      <#if compHelper.hasInitializerExpression(variable)>
        this.${variable.getName()} = ${compHelper.printExpression(compHelper.getInitializerExpression(variable))};
      </#if>
    </#list>
  }
</#macro>

<#macro printParametersAsList comp>
  <#list comp.getParameters() as param>
      <@getTypeString param.getType()/> ${param.getName()}<#t><#sep>, </#sep><#t>
  </#list>
</#macro>

<#macro getTypeString type boxPrimitives=false>
    <#if type.isPrimitive()><#t>
        <#if boxPrimitives>${type.getBoxedPrimitiveName()}<#t>
        <#else>${type.getPrimitiveName()}</#if><#t>
    <#elseif type.isTypeVariable()>${type.print()}<#t>
    <#else>${type.printFullName()}<#t>
    </#if>
</#macro>
