<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/logging/CompLogging.ftl" as Log>

<#assign hasOnlyOneVariant = helper.getVariantHelper().getVariants(ast)?size == 1>
<#assign hasModeAutomaton = helper.getComponentHelper().getModeAutomaton(ast).isPresent()>
<#-- Usually, the constructor visibility is protected to force users to use the builder
  -- for instantiating components. However, MAUnit components must have public
  -- constructors so that the test engine can instantiate them.
  -->
<#if ast.isPresentStereotype() && ast.getStereotype().contains("test")>
  <#assign visibility = "public">
<#else>
  <#assign visibility = "protected">
</#if>

${visibility} ${ast.getName()}${suffixes.compImpl()}<#if isTop>${suffixes.top()}</#if>(
  String name,
  montiarc.rte.scheduling.Scheduler scheduler,
  montiarc.rte.oracle.OracleFactory oracleFactory
  <#list ast.getHead().getArcParameterList()>,
    <#items as param><@Util.getTypeString param.getSymbol().getType()/> ${prefixes.parameter()}${param.getName()}<#sep>, </#items>
  </#list>
  <#list helper.getComponentHelper().getFeatures(ast)>,
    <#items as feature>Boolean ${prefixes.feature()}${feature.getName()}<#sep>, </#items>
  </#list>
) {
  super(name, scheduler <#if hasModeAutomaton>, oracleFactory</#if>);

  <#list ast.getHead().getArcParameterList() as param>
    this.${prefixes.parameter()}${param.getName()} = ${prefixes.parameter()}${param.getName()};
  </#list>
  <#list helper.getComponentHelper().getFeatures(ast) as feature>
    this.${prefixes.feature()}${feature.getName()} = ${prefixes.feature()}${feature.getName()};
  </#list>
  <#if hasModeAutomaton>
    this.modeAutomaton = new ${ast.getName()}${suffixes.modeAutomaton()}(this, name);
  </#if>

<#if !(hasOnlyOneVariant && javaPrinter.generateCodeCondition(helper.getVariantHelper().getVariants(ast)[0]) == "true")>
  this.variantID = determineVariant();
</#if>

${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

<#list helper.getComponentHelper().getFieldsInDependencyOrder(ast) as field>
  <#assign initExpr = javaPrinter.generateCode(helper.getComponentHelper().getInitialForVariable(field))>
  <@Util.getTypeString field.getType()/> ${field.getName()}${helper.getVariantHelper().fieldVariantSuffix(ast, field)} = ${initExpr};
</#list>

<#list ast.getSymbol().getFields() as field>
  ${prefixes.field()}${field.getName()}${helper.getVariantHelper().fieldVariantSuffix(ast, field)} = ${field.getName()}${helper.getVariantHelper().fieldVariantSuffix(ast, field)};
</#list>

  <#if hasOnlyOneVariant>
    <@variantSetup helper.getVariantHelper().getVariants(ast)[0]/>
  <#else>
    switch (this.variantID){
      <#list helper.getVariantHelper().getVariants(ast) as variant>
      case ${helper.getVariantHelper().variantSuffix(variant)} :
        <@variantSetup variant/>
        break;
      </#list>
      default:
        assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
    }
  </#if>

<#if hasModeAutomaton>
  this.modeAutomaton.setup();
</#if>

this.scheduler.register(this);
this.oracle = oracleFactory.createOracleFor(this.getName());

<@logInstantiation/>
}

<#macro variantSetup variant>
  this.isAtomic = ${(variant.isAtomic() && !hasModeAutomaton)?c};
  <@MethodNames.portSetup/>${helper.getVariantHelper().variantSuffix(variant)}();
  <#if variant.isAtomic()>
    <@MethodNames.behaviorSetup/>${helper.getVariantHelper().variantSuffix(variant)}();
  <#elseif variant.isDecomposed()>
    <@MethodNames.subCompSetup/>${helper.getVariantHelper().variantSuffix(variant)}(oracleFactory);
    <@MethodNames.connectorSetup/>${helper.getVariantHelper().variantSuffix(variant)}();
  </#if>
  <#if !hasModeAutomaton>
    <@MethodNames.setupUnconnectedOutPorts/>${helper.getVariantHelper().variantSuffix(variant)}();
  </#if>
</#macro>

<#macro logInstantiation>
<#assign hasParams = ast.getHead().getArcParameterList()?size != 0>
<#assign hasFeatures = helper.getComponentHelper().getFeatures(ast)?size != 0>
<#assign hasFields = ast.getSymbol().getFields()?size != 0>

<@Log.info log_aspects.createComponent(), "this.getName()">
  "${ast.getSymbol().getFullName()} with"
  <#if hasParams || hasFeatures || hasFields>
    + " {"
    <#list ast.getHead().getArcParameterList() as param>
      + "${param.getName()}=" + montiarc.rte.logging.DataFormatter.format(this.${prefixes.parameter()}${param.getName()})<#sep> + ", "
    </#list>
    <#if hasParams && (hasFeatures || hasFields)> + ", "</#if>  <#-- Separator between parameters and following stuff -->
    <#list helper.getComponentHelper().getFeatures(ast) as feature>
      + "${feature.getName()}=" + montiarc.rte.logging.DataFormatter.format(this.${prefixes.feature()}${feature.getName()})<#sep> + ", "
    </#list>
    <#if hasFeatures && hasFields> + ", " </#if>
    <#list ast.getSymbol().getFields() as field>
      + "${field.getName()}=" + montiarc.rte.logging.DataFormatter.format(this.${prefixes.field()}${field.getName()}${helper.getVariantHelper().fieldVariantSuffix(ast, field)})<#sep> + ", "
    </#list>
    + "};"
  </#if>
  <#if !hasOnlyOneVariant>
    + " variant hash = " + this.variantID + ";"
  </#if>
  + " scheduler type = " + this.scheduler.getClass().getSimpleName() + ";"
  + " oracle type = " + this.oracle.getClass().getSimpleName() + ";"
</@Log.info>
</#macro>
