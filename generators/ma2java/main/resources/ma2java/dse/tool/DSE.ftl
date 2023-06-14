<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop","lister")}

<#assign comp = ast.getSymbol()/>

<#if comp.getPackageName() != "">
	package ${comp.getPackageName()};
</#if>

import com.microsoft.z3.*;
import java.util.ArrayList;
import java.util.List;
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

		<#list comp.getAllIncomingPorts() as port>
			<#if compHelperDse.isEnum(port)>
				${port.getName()}.update(montiarc.rte.dse.AnnotatedValue.newAnnoValue(inputs.get(i).get${port.getName()}(), ${port.getTypeInfo().getFullName()}.valueOf((model.eval(inputs.get(i).get${port.getName()}(), true).toString().toString()))));
			<#elseif compHelperDse.isFloatOrDouble(port)>
				String evalModel = model.eval(inputs.get(i).get${port.getName()}(), true).toString();
				if(evalModel.contains("/")){
					<@printType port/> numerator = ${compHelperDse.getParseType(port)}(model.eval(inputs.get(i).get${port.getName()}(), true).toString().substring(0, model.eval(inputs.get(i).get${port.getName()}(), true).toString().lastIndexOf('/')));
					<@printType port/> denominator = ${compHelperDse.getParseType(port)}(model.eval(inputs.get(i).get${port.getName()}(), true).toString().substring(model.eval(inputs.get(i).get${port.getName()}(), true).toString().lastIndexOf('/')+1));
					${port.getName()}.update(montiarc.rte.dse.AnnotatedValue.newAnnoValue(inputs.get(i).get${port.getName()}(), numerator/denominator));
				}else{
					${port.getName()}.update(montiarc.rte.dse.AnnotatedValue.newAnnoValue(inputs.get(i).get${port.getName()}(), ${compHelperDse.getParseType(port)}(evalModel)));
				}
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
			<#list comp.getParameters() as parameter>
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
				comp.set${port.getName()?cap_first}(input.getKey().get(i).get${port.getName()}());
				</#list>
		<#else>
			for(int i=0; i<input.size(); i++){
				<#list comp.getAllIncomingPorts() as port>
					comp.set${port.getName()?cap_first}(input.get(i).get${port.getName()}());
				</#list>
		</#if>

			comp.compute();

		<#list comp.getAllOutgoingPorts() as port>
			 montiarc.rte.timesync.IOutPort< montiarc.rte.dse.AnnotatedValue<Expr<${compHelperDse.getPortTypeSort(port)}>, ${compHelper.getRealPortTypeString(port)}>> ${port.getName()} = new montiarc.rte.timesync.OutPort<>();
			 ${port.getName()}.setValue( montiarc.rte.dse.AnnotatedValue.newAnnoValue(comp.get${port.getName()?cap_first}().getValue().getExpr(), comp.get${port.getName()?cap_first}().getValue().getValue()));
		</#list>

		result.add(new ListerOut${comp.getName()}(
			<#list comp.getAllOutgoingPorts() as port>
				${port.getName()}
				<#sep> , </#sep>
			</#list>
		));
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