<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop", "lister")}

<#assign comp = ast.getSymbol()/>

<#assign strategies = compHelperDse.getPathStrategies()>
<#assign pathController = ["PathCoverageController"]>
<#if comp.getPackageName() != "">
	package ${comp.getPackageName()};
</#if>

import com.microsoft.z3.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import java.util.Set;
import main.DSEMain;
import montiarc.rte.dse.strategies.PathCoverageController;
import montiarc.rte.dse.ResultI;


<#if (strategies?size > 0)>
	import controller.*;
</#if>

public class DSEMain${comp.getName()} extends DSEMain{

	@Override
	public ResultI runController(String[] args) throws Exception {

		switch(args[1]){
			<#list strategies as strategie>
				<#if comp.hasParameters()>
					case "${strategie}":
						return run${strategie}(Integer.parseInt(args[2]), args);
				<#else>
				case "${strategie}":
					return run${strategie}(Integer.parseInt(args[2]));
				</#if>
			</#list>
			default:
				<#if comp.hasParameters()>
					return runPathCoverageController(Integer.parseInt(args[2]), args);
				<#else>
					return runPathCoverageController(Integer.parseInt(args[2]));
				</#if>
		}
	}

	<@printRunMethods comp pathController/>
	<@printRunMethods comp strategies/>
	<@printCreateInitialInput comp/>

}

<#macro printRunMethods comp strategies>
	<#list strategies as strategie>
		<#if comp.hasParameters()>
			public montiarc.rte.dse.ResultI<Pair<List<ListerIn${comp.getName()}>, ListerParameter${comp.getName()}>, List<ListerOut${comp.getName()}>> run${strategie}(int inputLength, String[] args) throws Exception {
		<#else>
			public montiarc.rte.dse.ResultI<List<ListerIn${comp.getName()}>, List<ListerOut${comp.getName()}>> run${strategie}(int inputLength) throws Exception {
		</#if>

			<#if comp.hasParameters()>
				montiarc.rte.dse.ControllerI<Pair<List<ListerIn${comp.getName()}>, ListerParameter${comp.getName()}>, List<ListerOut${comp.getName()}>>
			<#else>
				montiarc.rte.dse.ControllerI<List<ListerIn${comp.getName()}>, List<ListerOut${comp.getName()}>>
			</#if>

			controller = new ${strategie}<>();

			//initializing the controller
			controller.init();

			//create initial inputList
			List<ListerExpression${comp.getName()}> inputExpr = createInitialInput(inputLength);

			<#if comp.hasParameters()>
					<@printListerParameter comp/>
			</#if>

			Context ctx = controller.getCtx();
			Solver solver = ctx.mkSolver();
			solver.check();

			<#if comp.hasParameters()>
				<@printResultParameter comp/>
			<#else>
				montiarc.rte.dse.ResultI<List<ListerIn${comp.getName()}>, List<ListerOut${comp.getName()}>>
				result = controller.startTest(DSE${comp.getName()}.getInputValues(solver.getModel(), inputExpr),
				m -> DSE${comp.getName()}.getInputValues(m, inputExpr), DSE${comp.getName()}::runOnce);
			</#if>

			return result;
		}
	</#list>
</#macro>

<#macro printCreateInitialInput comp>
	public List<ListerExpression${comp.getName()}> createInitialInput(int inputLength){
		<@printEnumSorts ast/>

		List<ListerExpression${comp.getName()}> inputExpr = new ArrayList<>();

		<#list comp.getAllIncomingPorts() as port>
			Expr<${compHelperDse.getPortTypeSort(port)}> ${port.getName()} = null;
		</#list>

		for( int i = 0; i< inputLength; i++){
			<#list comp.getAllIncomingPorts() as port>
				<#if compHelperDse.isEnum(port)>
					${port.getName()} = montiarc.rte.dse.TestController.getCtx().mkConst("input${port.getName()}_" + i, ${port.getType().print()?lower_case});
				<#else>
					${port.getName()} = montiarc.rte.dse.TestController.getCtx().mkConst("input${port.getName()}_" + i, montiarc.rte.dse.TestController.getCtx().mk${compHelperDse.getMkSort(port)}());
				</#if>
			</#list>

			inputExpr.add(new ListerExpression${comp.getName()}(
			<#list comp.getAllIncomingPorts() as port>
				${port.getName()}
				<#sep> , </#sep>
			</#list>
			));
		}
		return inputExpr;
	}
</#macro>

<#macro printListerIn comp>
	<#list comp.getAllIncomingPorts() as port>
		montiarc.rte.timesync.IInPort<montiarc.rte.dse.AnnotatedValue<Expr<${compHelperDse.getPortTypeSort(port)}>, ${compHelper.getRealPortTypeString(port)}>>
		<#sep> , </#sep>
	</#list>
</#macro>

<#macro printListerInSingle comp>
	<#list comp.getAllIncomingPorts() as port>
		Expr<${compHelperDse.getPortTypeSort(port)}>
		<#sep> , </#sep>
	</#list>
</#macro>

<#macro printListerOut comp>
	<#list comp.getAllOutgoingPorts() as port>
		montiarc.rte.timesync.IOutPort<montiarc.rte.dse.AnnotatedValue<Expr<${compHelperDse.getPortTypeSort(port)}>, ${compHelper.getRealPortTypeString(port)}>>
		<#sep> , </#sep>
	</#list>
</#macro>

<#macro printEnumSorts ast>
	<#list compHelperDse.getPortTypes(ast.getPorts()) as port>
		<#if compHelperDse.isEnum(port.getSymbol())>
			EnumSort<${port.getSymbol().getTypeInfo().getFullName()}> ${port.getSymbol().getType().print()?lower_case} = montiarc.rte.dse.TestController.getCtx().mkEnumSort("${port.getSymbol().getType().print()}",
				<#list compHelperDse.getEnumValues(port) as value>
					"${value.getName()}"
					<#sep> , </#sep>
				</#list>
			);
		</#if>
	</#list>
</#macro>

<#macro printListerParameter comp>
	ListerParameter${comp.getName()} parameters = new ListerParameter${comp.getName()}(
		<#assign x = 3>
		<#list comp.getParametersList() as parameter>
			montiarc.rte.dse.AnnotatedValue.newAnnoValue(
			<#if compHelperDse.isEnum(parameter)>
				${parameter.getType().print()?lower_case}.getConst(getEnumIndex(args[${x}], ${parameter.getType().print()?lower_case}) , args[${x}])
			<#elseif compHelperDse.isCharacter(parameter) >
				montiarc.rte.dse.TestController.getCtx().${compHelperDse.getParameterType(parameter)}( args[${x}]', 18))), ${compHelperDse.getParseType(parameter)}(args[${x}])${compHelperDse.printCharReplace(port)}))
			<#else>
				montiarc.rte.dse.TestController.getCtx().${compHelperDse.getParameterType(parameter)}( args[${x}])), ${compHelperDse.getParseType(parameter)}(args[${x}]))
			</#if>
			<#assign x++>
			<#sep> , </#sep>
		</#list>
	);
</#macro>

<#macro printResultParameter comp>
	montiarc.rte.dse.ResultI<Pair<List<ListerIn${comp.getName()}>, ListerParameter${comp.getName()}>, List<ListerOut${comp.getName()}>>
	result = controller.startTest(DSE${comp.getName()}.getInputValues(solver.getModel(), inputExpr, parameters),
	m -> DSE${comp.getName()}.getInputValues(m, inputExpr, parameters), DSE${comp.getName()}::runOnce);
</#macro>