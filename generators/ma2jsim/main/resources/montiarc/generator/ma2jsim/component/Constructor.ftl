<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast-->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/logging/CompLogging.ftl" as Log>

<#assign hasOnlyOneVariant = helper.getVariants(ast)?size == 1>

protected ${ast.getName()}${suffixes.component()}<#if isTop>${suffixes.top()}</#if>(
  String name,
  montiarc.rte.scheduling.Scheduler scheduler
  <#list ast.getHead().getArcParameterList()>,
    <#items as param><@Util.getTypeString param.getSymbol().getType()/> ${prefixes.parameter()}${param.getName()}<#sep>, </#items>
  </#list>
  <#list helper.getFeatures(ast)>,
    <#items as feature>Boolean ${prefixes.feature()}${feature.getName()}<#sep>, </#items>
  </#list>
) {
  super(name, scheduler);

  <#list ast.getHead().getArcParameterList() as param>
    this.${prefixes.parameter()}${param.getName()} = ${prefixes.parameter()}${param.getName()};
  </#list>
  <#list helper.getFeatures(ast) as feature>
    this.${prefixes.feature()}${feature.getName()} = ${prefixes.feature()}${feature.getName()};
  </#list>
  <#if helper.getModeAutomaton(ast).isPresent()>
    this.modeAutomaton = new ${ast.getName()}${suffixes.modeAutomaton()}(this);
  </#if>

<#if !(hasOnlyOneVariant && prettyPrinter.prettyprintCondition(helper.getVariants(ast)[0]) == "true")>
  this.variantID = determineVariant();
</#if>

${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

<#list ast.getSymbol().getFields() as field>
  ${prefixes.field()}${field.getName()}${helper.fieldVariantSuffix(ast, field)} = ${prettyPrinter.prettyprint(helper.getInitialForVariable(field))};
</#list>

  <#if hasOnlyOneVariant>
    <@variantSetup helper.getVariants(ast)[0]/>
  <#else>
    switch (this.variantID){
      <#list helper.getVariants(ast) as variant>
      case ${helper.variantSuffix(variant)} :
        <@variantSetup variant/>
        break;
      </#list>
      default:
        assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
    }
  </#if>

<#if helper.getModeAutomaton(ast).isPresent()>
  this.modeAutomaton.setup();
</#if>

this.scheduler.register(this, this.getAllInPorts(), isSync);

<@logInstantiation/>
}

<#macro variantSetup variant>
  this.isAtomic = ${(variant.isAtomic() && !helper.getModeAutomaton(ast).isPresent())?c};
  <@MethodNames.portSetup/>${helper.variantSuffix(variant)}();
  <#if variant.isAtomic()>
    <@MethodNames.behaviorSetup/>${helper.variantSuffix(variant)}();
  <#elseif variant.isDecomposed()>
    <@MethodNames.subCompSetup/>${helper.variantSuffix(variant)}();
    <@MethodNames.connectorSetup/>${helper.variantSuffix(variant)}();
  </#if>
  <#if !helper.getModeAutomaton(ast).isPresent()>
    <@MethodNames.setupUnconnectedOutPorts/>${helper.variantSuffix(variant)}();
  </#if>
</#macro>

<#macro logInstantiation>
<#assign hasParams = ast.getHead().getArcParameterList()?size != 0>
<#assign hasFeatures = helper.getFeatures(ast)?size != 0>
<#assign hasFields = ast.getSymbol().getFields()?size != 0>

<@Log.info log_aspects.createComponent(), "this.getName()">
  "${ast.getSymbol().getFullName()} with"
  <#if hasParams || hasFeatures || hasFields>
    + " {"
    <#list ast.getHead().getArcParameterList() as param>
      + "${param.getName()}=" + montiarc.rte.logging.DataFormatter.format(this.${prefixes.parameter()}${param.getName()})<#sep> + ", "
    </#list>
    <#if hasParams && (hasFeatures || hasFields)> + ", "</#if>  <#-- Separator between parameters and following stuff -->
    <#list helper.getFeatures(ast) as feature>
      + "${feature.getName()}=" + montiarc.rte.logging.DataFormatter.format(this.${prefixes.feature()}${feature.getName()})<#sep> + ", "
    </#list>
    <#if hasFeatures && hasFields> + ", " </#if>
    <#list ast.getSymbol().getFields() as field>
      + "${field.getName()}=" + montiarc.rte.logging.DataFormatter.format(this.${prefixes.field()}${field.getName()}${helper.fieldVariantSuffix(ast, field)})<#sep> + ", "
    </#list>
    + "};"
  </#if>
  <#if !hasOnlyOneVariant>
    + " variant hash = " + this.variantID + ";"
  </#if>
  + " scheduler type = " + this.scheduler.getClass().getSimpleName() + ";"
</@Log.info>
</#macro>