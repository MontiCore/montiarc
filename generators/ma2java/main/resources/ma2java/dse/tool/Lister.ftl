<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop", "lister")}

<#assign comp = ast.getSymbol()/>

<#assign list = getLister(lister) >

<#if comp.getPackageName() != "">
  package ${comp.getPackageName()};
</#if>

import com.microsoft.z3.*;
import montiarc.rte.dse.TestController;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class ${lister}${comp.getName()} implements montiarc.rte.dse.ListerI{

	<@printEnumSorts ast/>

	<#list list as port>
		private final <@printElementType lister/><${compHelperDse.getPortTypeSort(port)}> <@printElementTypeEnd lister port/> ${port.getName()};
	</#list>

	public ${lister}${comp.getName()}(
		<#list list as port>
			<@printElementType lister/><${compHelperDse.getPortTypeSort(port)}> <@printElementTypeEnd lister port/> ${port.getName()}
			<#sep> , </#sep>
		</#list>
		){
		<#list list as port>
		this.${port.getName()} = ${port.getName()};
		</#list>
	}

	<#list list as port>
		public <@printElementType lister/><${compHelperDse.getPortTypeSort(port)}> <@printElementTypeEnd lister port/> get${port.getName()}(){
			return this.${port.getName()};
		}
	</#list>

	<@printGetEntries lister comp/>

	<@printGetExpression lister comp/>

	<@printEquals lister comp/>
}

<#macro printEquals lister comp>
@Override
public boolean equals(Object ob){
	if(ob==this){
		return true;
	}
	if(ob == null || ob.getClass() != getClass()){
		return false;
	}

	${lister}${comp.getName()} listerExpr = (${lister}${comp.getName()}) ob;

	<#if getLister(lister)?size == 0 >
		return false;
	<#else>
		return Objects.equals(
    			<#list getLister(lister) as element>
    				get${element.getName()}(), listerExpr.get${element.getName()}()
    				<#sep> ) && Objects.equals( </#sep>
    			</#list>
    		);
	</#if>
 }
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

<#macro printEnumSorts ast>
	<#list compHelperDse.getPortTypes(ast.getPorts()) as port>
		<#if compHelperDse.isEnum(port.getSymbol())>
			EnumSort<${port.getSymbol().getType().printFullName()}> ${port.getSymbol().getType().print()?lower_case} = montiarc.rte.dse.TestController.getCtx().mkEnumSort("${port.getSymbol().getType().print()}",
				<#list compHelperDse.getEnumValues(port) as value>
					"${value.getName()}"
					<#sep> , </#sep>
				</#list>
			);
		</#if>
	</#list>
</#macro>

<#function getLister lister >
	<#if lister == "ListerExpression" || lister == "ListerIn">
		<#return comp.getAllIncomingPorts()>
	<#elseif lister == "ListerOut" || lister == "ListerExprOut">
		<#return comp.getAllOutgoingPorts()>
	<#elseif lister == "ListerParameter">
		<#return comp.getParameters()>
	<#else>
		<#return comp.getParameters()>
	</#if>
</#function>

<#macro printElementType lister >
	<#if lister == "ListerExpression" || lister = "ListerExprOut">
		 Expr
	<#elseif lister == "ListerIn">
		montiarc.rte.timesync.IInPort<montiarc.rte.dse.AnnotatedValue<Expr
	<#elseif lister == "ListerOut">
		montiarc.rte.timesync.IOutPort<montiarc.rte.dse.AnnotatedValue<Expr
	<#elseif lister == "ListerParameter">
		montiarc.rte.dse.AnnotatedValue<Expr
	<#else>
		${lister}
	</#if>
</#macro>

<#macro printElementTypeEnd lister port >
	<#if lister == "ListerExpression" || lister == "ListerExprOut">
	<#elseif lister == "ListerIn">
		,<@printType port/>>>
	<#elseif lister == "ListerOut">
		,<@printType port/>>>
	<#elseif lister == "ListerParameter">
		,<@printType port/>>
	<#else>
		${lister}
	</#if>
</#macro>

<#macro printGetEntries lister comp>
	<#if lister == "ListerExpression" || lister == "ListerExprOut">
	<#elseif lister == "ListerParameter">
	<#if comp.hasParameters() >
		@Override
		public String getEntries() {
			return "[" +
				<#list comp.getParameters() as port>
					"<" + ${port.getName()}.getExpr().toString() + ", " + ${port.getName()}.getValue().toString() + ">"
					<#sep> + "|" + </#sep>
				</#list>
			+ "]";
		}
		</#if>
	<#else>
		@Override
		public String getEntries() {
			return "[" +
				<#list list as port>
					"<" + ${port.getName()}.getValue().getExpr().toString() + ", " + ${port.getName()}.getValue().getValue().toString() + ">"
					<#sep> + "|" + </#sep>
				</#list>
			+ "]";
		}
	</#if>
</#macro>

<#macro	printGetExpression lister comp>
	<#if lister == "ListerOut">
	public ListerExprOut${comp.getName()} getExpression(){
		<#list list as element>
			Expr<${compHelperDse.getPortTypeSort(element)}> result${element.getName()} = null;
		</#list>

		<#list list as element>
			if(this.${element.getName()}.getValue() != null){
				result${element.getName()} = this.${element.getName()}.getValue().getExpr().simplify();
			}
		</#list>

		return new ListerExprOut${comp.getName()}(
				<#list list as element>
					result${element.getName()}
					<#sep> , </#sep>
				</#list>
				);
	}
	</#if>
</#macro>

<#macro printGetEntries lister comp>
	@Override
	public String getEntries(){
	<#if !comp.hasParameters() && lister == "ListerParameter">
		return null;
	<#else>
			return
			<#list list as port>
				this.${port.getName()}.toString()
				<#sep> + </#sep>
			</#list>
			;
	</#if>
	}
</#macro>