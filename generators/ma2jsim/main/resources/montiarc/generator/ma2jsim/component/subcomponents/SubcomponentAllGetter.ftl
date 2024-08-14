<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign modeAutomatonOpt = helper.getModeAutomaton(ast)>

public java.util.List${"<"}montiarc.rte.component.IComponent${">"} getAllSubcomponents() {
${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

  final java.util.ArrayList${"<"}montiarc.rte.component.IComponent${">"} __allSubcomponentList__ = new java.util.ArrayList<>();
<#list ast.getSymbol().getSubcomponents() as subcomponent>
  <#assign existenceConditions = helper.getExistenceCondition(ast, subcomponent)/>
  <#if existenceConditions?has_content>if(${prettyPrinter.prettyprint(existenceConditions)}) {</#if>
  __allSubcomponentList__.add(${prefixes.subcomp()}${subcomponent.getName()}${helper.subcomponentVariantSuffix(ast, subcomponent)}());
  <#if existenceConditions?has_content>}</#if>
</#list>

  <#if modeAutomatonOpt.isPresent()>
  <#list helper.getModes(modeAutomatonOpt.get()) as mode>
    <#list helper.getInstancesFromMode(mode) as sub>
    <#assign subSymbol = sub.getSymbol()>
    <#assign subCompName>this.${prefixes.subcomp()}${mode.getName()}_${subSymbol.getName()}${helper.subcomponentVariantSuffix(ast, subSymbol)}</#assign>
    if (${subCompName}() != null) {
      __allSubcomponentList__.add(${subCompName}());
    }
    </#list>
  </#list>
  </#if>

  return __allSubcomponentList__;
}