<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

@Override
public java.util.List${"<"}montiarc.rte.port.ITimeAwareInPort${"<?>>"} getAllInPorts() {
${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

final java.util.ArrayList${"<"}montiarc.rte.port.ITimeAwareInPort${"<?>>"} __allInPortList__ = new java.util.ArrayList<>();
<#list ast.getSymbol().getAllIncomingPorts() as portSym>
  <#assign existenceConditions = helper.getExistenceCondition(ast, portSym)/>
  <#if existenceConditions?has_content>if(${prettyPrinter.prettyprint(existenceConditions)}) {</#if>
  __allInPortList__.add(${prefixes.port()}${portSym.getName()}${helper.portVariantSuffix(ast, portSym)}());
  <#if existenceConditions?has_content>}</#if>
</#list>
return __allInPortList__;
}

@Override
public java.util.List${"<"}montiarc.rte.port.TimeAwareOutPort${"<?>>"} getAllOutPorts() {
${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

final java.util.ArrayList${"<"}montiarc.rte.port.TimeAwareOutPort${"<?>>"} __allOutPortList__ = new java.util.ArrayList<>();
<#list ast.getSymbol().getAllOutgoingPorts() as portSym>
  <#assign existenceConditions = helper.getExistenceCondition(ast, portSym)/>
  <#if existenceConditions?has_content>if(${prettyPrinter.prettyprint(existenceConditions)}) {</#if>
  __allOutPortList__.add(${prefixes.port()}${portSym.getName()}${helper.portVariantSuffix(ast, portSym)}());
  <#if existenceConditions?has_content>}</#if>
</#list>
return __allOutPortList__;
}