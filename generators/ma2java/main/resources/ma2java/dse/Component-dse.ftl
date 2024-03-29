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

	<@printInternalStates ast/>

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
    <#if !comp.isEmptySuperComponents()>
      super(<#list comp.getParentConfiguration(comp.getSuperComponents(0)) as parentConfiguration>${compHelperDse.printExpression(parentConfiguration.getExpression())}<#sep>, </#sep></#list>);
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

<#macro printInternalStates comp>
	public montiarc.rte.dse.StatesList getInternalStates(){
		<#if !comp.getSymbol().isDecomposed()>
			List<String> result = new ArrayList(){{
			<#list compHelper.getComponentVariables(comp.getSymbol()) as variable>
				add("${variable.getName()}: " + ${variable.getName()}.toString());
			</#list>
			}};

			List<montiarc.rte.dse.StateInfo> info = new ArrayList(){{
				<#if compHelper.getAutomatonBehavior(ast).isPresent()>
					<#assign automaton = compHelper.getAutomatonBehavior(ast).get()>
					<#if automaton.getStates()?size != 0>
						add(montiarc.rte.dse.StateInfo.newStateInfo(currentState, result, instanceName));
					</#if>
				</#if>
			}};
		<#else>
			List<montiarc.rte.dse.StateInfo> info = new ArrayList(){{
				<#list comp.getSubComponents() as innerComp>
					addAll(${innerComp.getName()}.getInternalStates().getStates());
				</#list>
			}};
		</#if>
		return montiarc.rte.dse.StatesList.newStatesList(info);
	}
</#macro>
