<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop","lister")}

<#assign comp = ast.getSymbol()/>

<#assign tickStatementList = []/>
<#assign tickStatementListif = []/>

<#if comp.getPackageName() != "">
	package ${comp.getPackageName()};
</#if>

import com.microsoft.z3.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import montiarc.rte.dse.TestController;

public class DSE${comp.getName()}{

<#if comp.hasParameters()>
	public static Pair<List<ListerIn${comp.getName()}>, ListerParameter${comp.getName()}> getInputValues(
		Model model, List<ListerExpression${comp.getName()}> inputs, ListerParameter${comp.getName()} parameters){
<#else>
	public static List<ListerIn${comp.getName()}> getInputValues(
		Model model, List<ListerExpression${comp.getName()}> inputs){
</#if>
		List<ListerIn${comp.getName()}> result = new ArrayList<>();

		<@printEnumSorts ast/>

		<#if comp.hasParameters()>
			for(int i=0; i<inputs.size(); i++){
				<#list comp.getAllIncomingPorts() as port>
					 montiarc.rte.timesync.IInPort< montiarc.rte.dse.AnnotatedValue<Expr<${compHelperDse.getPortTypeSort(port)}>, ${compHelper.getRealPortTypeString(port)}>> ${port.getName()} = new montiarc.rte.timesync.InPort<>();
				</#list>
		<#else>
			for(int i=0; i<inputs.size(); i++){
				<#list comp.getAllIncomingPorts() as port>
					 montiarc.rte.timesync.IInPort< montiarc.rte.dse.AnnotatedValue<Expr<${compHelperDse.getPortTypeSort(port)}>, ${compHelper.getRealPortTypeString(port)}>> ${port.getName()} = new montiarc.rte.timesync.InPort<>();
				</#list>
		</#if>

		String evalModel;
		<#list comp.getAllIncomingPorts() as port>
			<#if compHelperDse.isEnum(port)>
				${port.getName()}.update(montiarc.rte.dse.AnnotatedValue.newAnnoValue(inputs.get(i).get${port.getName()}(), ${port.getTypeInfo().getFullName()}.valueOf((model.eval(inputs.get(i).get${port.getName()}(), true).toString().toString()))));
			<#elseif compHelperDse.isFloatOrDouble(port)>
				evalModel = model.eval(inputs.get(i).get${port.getName()}(), true).toString();
				if(evalModel.contains("/")){
					<@printType port/> numerator = ${compHelperDse.getParseType(port)}(model.eval(inputs.get(i).get${port.getName()}(), true).toString().substring(0, model.eval(inputs.get(i).get${port.getName()}(), true).toString().lastIndexOf('/')));
					<@printType port/> denominator = ${compHelperDse.getParseType(port)}(model.eval(inputs.get(i).get${port.getName()}(), true).toString().substring(model.eval(inputs.get(i).get${port.getName()}(), true).toString().lastIndexOf('/')+1));
					${port.getName()}.update(montiarc.rte.dse.AnnotatedValue.newAnnoValue(inputs.get(i).get${port.getName()}(), numerator/denominator));
				}else{
					${port.getName()}.update(montiarc.rte.dse.AnnotatedValue.newAnnoValue(inputs.get(i).get${port.getName()}(), ${compHelperDse.getParseType(port)}(evalModel)));
				}
			<#elseif compHelperDse.isString(port)>
				evalModel = (model.eval(inputs.get(i).get${port.getName()}(), true)).toString();
				${port.getName()}.update(montiarc.rte.dse.AnnotatedValue.newAnnoValue(inputs.get(i).get${port.getName()}(), evalModel.substring(1, evalModel.length() - 1)));
			<#else>
				${port.getName()}.update(montiarc.rte.dse.AnnotatedValue.newAnnoValue(inputs.get(i).get${port.getName()}(), ${compHelperDse.getParseType(port)}(model.eval(inputs.get(i).get${port.getName()}(), true).toString()${compHelperDse.printCharReplace(port)})));
			</#if>
		</#list>

		result.add(new ListerIn${comp.getName()}(
			<#list comp.getAllIncomingPorts() as port>
					${port.getName()}
				 <#sep> , </#sep>
			</#list>
		));
		}

		<#if comp.hasParameters()>
			return ImmutablePair.of(result, parameters);
		<#else>
			return result;
		</#if>
	}

<#if comp.hasParameters()>
	public static List<ListerOut${comp.getName()}> runOnce(Pair<List<ListerIn${comp.getName()}>, ListerParameter${comp.getName()}> input){

		${comp.getName()} comp = new ${comp.getName()}(
			<#list comp.getParametersList() as parameter>
				input.getValue().get${parameter.getName()}()
				<#sep> , </#sep>
			</#list>
		);
<#else>
	public static List<ListerOut${comp.getName()}> runOnce(List<ListerIn${comp.getName()}>input){

		${comp.getName()} comp = new ${comp.getName()}();
</#if>

		comp.setUp();
		comp.init();
		comp.setInstanceName("${comp.getName()}");

		List<ListerOut${comp.getName()}>result = new ArrayList<>();

		<#if comp.hasParameters()>
			for(int i=0; i<input.getKey().size(); i++){
				<#list comp.getAllIncomingPorts() as port>
				comp.get${port.getName()?cap_first}().update(input.getKey().get(i).get${port.getName()}().getValue());
				</#list>
		<#else>
			for(int i=0; i<input.size(); i++){
				<#list comp.getAllIncomingPorts() as port>
					comp.get${port.getName()?cap_first}().update(input.get(i).get${port.getName()}().getValue());
				</#list>
		</#if>

		comp.compute();

		<#list comp.getAllOutgoingPorts() as port>
			montiarc.rte.timesync.IOutPort< montiarc.rte.dse.AnnotatedValue<Expr<${compHelperDse.getPortTypeSort(port)}>, ${compHelper.getRealPortTypeString(port)}>> ${port.getName()} = new montiarc.rte.timesync.OutPort<>();
			if(comp.get${port.getName()?cap_first}().getValue() != null){
				${port.getName()}.setValue( montiarc.rte.dse.AnnotatedValue.newAnnoValue(comp.get${port.getName()?cap_first}().getValue().getExpr(), comp.get${port.getName()?cap_first}().getValue().getValue()));
			}else{
				${port.getName()}.setValue( null);
			}
		</#list>

		result.add(new ListerOut${comp.getName()}(
			<#list comp.getAllOutgoingPorts() as port>
				${port.getName()}
				<#sep> , </#sep>
			</#list>
		));

		<@printTick comp/>
		TestController.saveStates(comp.getInternalStates());
		if(TestController.shouldEndRun()){
        return new ArrayList<>();
		}

		}
		return result;
	}
}

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

<#macro printType port>
	<#if port.getType().isPrimitive()>
		${compHelper.boxPrimitive(port.getType())}
	<#elseif port.getType().isTypeVariable()>
		${port.getType().print()}
	<#else>
		${port.getType().printFullName()}
	</#if>
</#macro>

<#macro printTick comp>
	<#assign tickStatement = "comp"/>
	<#list comp.getPortsList() as port>
		<#if port.isDelayed()>
			<#assign item = tickStatement + "." + port.getName() />
			<#assign tickStatementList = tickStatementList + [item] />
		</#if>
	</#list>

	<#if comp.isDecomposed()>
		<#list ast.getSubComponents() as inner>
		<#assign innerComp = inner.getSymbol().getType().getTypeInfo()>
			<#assign tickStatements = tickStatement + ".getComponent${inner.getName()?cap_first}()"/>
			<#list innerComp.getPortsList() as port>
				<#if port.isDelayed()>
					<@printTickDecomposed port tickStatements/>
				</#if>
			</#list>

			<#if innerComp.isDecomposed()>
				<#list innerComp.getSubcomponents() as innerComps>
				<#assign tickPath = tickStatements + ".getComponent${innerComps.getName()?cap_first}()"/>
						<@printInnerCompTick innerComps.getType().getTypeInfo() tickPath/>
				</#list>
			</#if>
		</#list>
	</#if>

Set<montiarc.rte.timesync.IOutPort> delayedPorts = new HashSet<>();

	<#list tickStatementList?reverse as item>
		delayedPorts.add(${item});
	</#list>

	for(montiarc.rte.timesync.IOutPort port : delayedPorts){
		port.tick();
	}
</#macro>

<#macro printTickDecomposed port ticksPath>
	<#if port.isDelayed()>
		<#assign item = ticksPath + ".get" + port.getName()?cap_first + "()"/>
		<#assign tickStatementList = tickStatementList + [item] />
	</#if>
</#macro>

<#macro printInnerCompTick (comp varTickStatement)>
	<#list comp.getPortsList() as port>
		<#if port.isDelayed()>
			<@printTickDecomposed port varTickStatement/>
		</#if>
	</#list>

	<#if comp.isDecomposed()>
		<#list comp.getSubcomponents() as innerComp>
			<#assign tickPaths = varTickStatement + ".getComponent${innerComp.getName()?cap_first}()"/>

			<@printInnerCompTick innerComp.getType().getTypeInfo() tickPaths/>
		</#list>
	</#if>
</#macro>