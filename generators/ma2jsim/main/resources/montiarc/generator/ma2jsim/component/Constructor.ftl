<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast-->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
protected ${ast.getName()}${suffixes.component()}<#if isTop>${suffixes.top()}</#if>(
  String name,
  montiarc.rte.scheduling.ISchedule scheduler
  <#list ast.getHead().getArcParameterList()>,
    <#items as param><@Util.getTypeString param.getSymbol().getType()/> ${prefixes.parameter()}${param.getName()}<#sep>, </#items>
  </#list>
  <#list helper.getFeatures(ast)>,
    <#items as feature>Boolean ${prefixes.feature()}${feature.getName()}<#sep>, </#items>
  </#list>
) {
  this.name = name;
  this.scheduler = scheduler;
  this.scheduler.register(this);
  <#list ast.getHead().getArcParameterList() as param>
    this.${prefixes.parameter()}${param.getName()} = ${prefixes.parameter()}${param.getName()};
  </#list>
  <#list helper.getFeatures(ast) as feature>
    this.${prefixes.feature()}${feature.getName()} = ${prefixes.feature()}${feature.getName()};
  </#list>

${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

<#list helper.getVariants(ast) as variant>
  if (${prettyPrinter.prettyprintCondition(variant)}) {
    this.isAtomic = ${variant.isAtomic()?c};
    <@MethodNames.portSetup/>${helper.variantSuffix(variant)}();
    <#if variant.isAtomic()>
        <@MethodNames.behaviorSetup/>${helper.variantSuffix(variant)}();
    <#elseif variant.isDecomposed()>
        <@MethodNames.subCompSetup/>${helper.variantSuffix(variant)}();
        <@MethodNames.connectorSetup/>${helper.variantSuffix(variant)}();
    </#if>
  }
  <#sep> else </#sep>
</#list>
else throw new java.lang.RuntimeException(
"Component ${ast.getName()} is not correctly configured, no variant selected");
}