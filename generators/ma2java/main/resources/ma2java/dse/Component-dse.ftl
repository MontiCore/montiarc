<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("isTop")}

${tc.includeArgs("ma2java.component.Header.ftl", ast, compHelper.asList(isTop))} {

  <@printInstanceName/>

  // ports
  ${tc.include("ma2java.component.Port.ftl", ast.getPorts())}

  <@printEnumSorts ast/>

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
    protected final montiarc.rte.dse.AnnotatedValue<Expr<${compHelperDse.getPortTypeSort(param)}>,<@printType param/>> ${param.getName()};
  </#list>
</#macro>

<#macro printVariables comp>
  <#list compHelper.getComponentVariables(comp) as variable>
    protected montiarc.rte.dse.AnnotatedValue<Expr<${compHelperDse.getPortTypeSort(variable)}>,<@printType variable/>> ${variable.getName()};
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
    <#if comp.isPresentParent()>
      super(<#list comp.getParentConfiguration() as parentConfiguration>${compHelperDse.printExpression(parentConfiguration.getExpression())}<#sep>, </#sep></#list>);
    </#if>

    // Context for Solver
    Context ctx = montiarc.rte.dse.TestController.getCtx();

    <#list comp.getParameters() as param>
      this.${param.getName()} = ${param.getName()};
      montiarc.rte.log.Log.trace("${param.getName()} Expr:" +  ${param.getName()}.getExpr() + " ${param.getName()} Value: " +  ${param.getName()}.getValue());
    </#list>

    <#list compHelper.getComponentVariables(comp) as variable>
      <#if compHelper.hasInitializerExpression(variable)>
        this.${variable.getName()} = montiarc.rte.dse.AnnotatedValue.newAnnoValue(${compHelperDse.printExpression(compHelper.getInitializerExpression(variable))}, ${compHelperDseValue.printExpression(compHelper.getInitializerExpression(variable))});
        montiarc.rte.log.Log.trace("${variable.getName()} Expr:" +  ${variable.getName()}.getExpr() + " ${variable.getName()} Value: " +  ${variable.getName()}.getValue());
      </#if>
    </#list>
  }
</#macro>

<#macro printParametersAsList comp>
  <#list comp.getParameters() as param>
    montiarc.rte.dse.AnnotatedValue<Expr<${compHelperDse.getPortTypeSort(param)}>,<@printType param/>> ${param.getName()}<#t><#sep>, </#sep><#t>
  </#list>
</#macro>

<#macro printEnumSorts ast>
	<#list compHelperDse.getPortTypes(ast.getPorts()) as port>
		<#if compHelperDse.isEnum(port.getSymbol())>
			EnumSort<${port.getSymbol().getType().print()}> ${port.getSymbol().getType().print()?lower_case} = montiarc.rte.dse.TestController.getCtx().mkEnumSort("${port.getSymbol().getType().print()}",
				<#list compHelperDse.getEnumValues(port) as value>
					"${value.getName()}"
					<#sep> , </#sep>
				</#list>
			);
		</#if>
	</#list>
	<#list compHelperDse.getEnumSorts(ast) as parameter>
			<#if compHelperDse.isEnum(parameter)>
				EnumSort<${parameter.getType().print()}> ${parameter.getType().print()?lower_case} = montiarc.rte.dse.TestController.getCtx().mkEnumSort("${parameter.getType().print()}",
					<#list compHelperDse.getEnumValues(parameter) as value>
						"${value.getName()}"
						<#sep> , </#sep>
					</#list>
				);
			</#if>
		</#list>
</#macro>

<#macro printType port>
  <#if port.getType().isPrimitive()>
    ${compHelper.boxPrimitive(port.getType())}
  <#elseif port.getType().isTypeVariable()>
    ${port.getType().print()}
  <#else>
    ${port.getType().printFullName()}
  </#if>
</#macro>