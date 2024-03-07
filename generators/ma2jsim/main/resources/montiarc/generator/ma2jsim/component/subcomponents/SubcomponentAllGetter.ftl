<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

public java.util.List${"<"}montiarc.rte.component.ITimedComponent${">"} getAllSubcomponents() {
${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

  final java.util.ArrayList${"<"}montiarc.rte.component.ITimedComponent${">"} __allSubcomponentList__ = new java.util.ArrayList<>();
<#list ast.getSymbol().getSubcomponents() as subcomponent>
  <#assign existenceConditions = helper.getExistenceCondition(ast, subcomponent)/>
  <#if existenceConditions?has_content>if(${prettyPrinter.prettyprint(existenceConditions)}) {</#if>
  __allSubcomponentList__.add(${prefixes.subcomp()}${subcomponent.getName()}${helper.subcomponentVariantSuffix(ast, subcomponent)}());
  <#if existenceConditions?has_content>}</#if>
</#list>
  return __allSubcomponentList__;
}