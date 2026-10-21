<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/logging/CompLogging.ftl" as Log>

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
) {
  super(name, scheduler <#if hasModeAutomaton>, oracleFactory</#if>);

  <#list ast.getHead().getArcParameterList() as param>
    this.${prefixes.parameter()}${param.getName()} = ${prefixes.parameter()}${param.getName()};
  </#list>
  <#if hasModeAutomaton>
    this.modeAutomaton = new ${ast.getName()}${suffixes.modeAutomaton()}(this, name);
  </#if>

${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

<#list helper.getComponentHelper().getFieldsInDependencyOrder(ast) as field>
  <#assign initExpr = javaPrinter.generateCode(helper.getComponentHelper().getInitialForVariable(field))>
  <@Util.getTypeString field.getType()/> ${field.getName()} = ${initExpr};
</#list>

<#list ast.getSymbol().getFields() as field>
  ${prefixes.field()}${field.getName()} = ${field.getName()};
</#list>

  this.isAtomic = ${(ast.getSymbol().isAtomic() && !hasModeAutomaton)?c};
  <@MethodNames.portSetup/>();
  <#if ast.getSymbol().isAtomic()>
    <@MethodNames.behaviorSetup/>();
  <#elseif ast.getSymbol().isDecomposed()>
    <@MethodNames.subCompSetup/>(oracleFactory);
    <@MethodNames.connectorSetup/>();
  </#if>
  <#if !hasModeAutomaton>
    <@MethodNames.setupUnconnectedOutPorts/>();
  </#if>

<#if hasModeAutomaton>
  this.modeAutomaton.setup();
</#if>

this.scheduler.register(this);
this.oracle = oracleFactory.createOracleFor(this.getName());

<@logInstantiation/>
}

<#macro logInstantiation>
<#assign hasParams = ast.getHead().getArcParameterList()?size != 0>
<#assign hasFields = ast.getSymbol().getFields()?size != 0>

<@Log.info log_aspects.createComponent(), "this.getName()">
  "${ast.getSymbol().getFullName()} with"
  <#if hasParams || hasFields>
    + " {"
    <#list ast.getHead().getArcParameterList() as param>
      + "${param.getName()}=" + montiarc.rte.logging.DataFormatter.format(this.${prefixes.parameter()}${param.getName()})<#sep> + ", "
    </#list>
    <#if hasParams && hasFields> + ", "</#if>
    <#list ast.getSymbol().getFields() as field>
      + "${field.getName()}=" + montiarc.rte.logging.DataFormatter.format(this.${prefixes.field()}${field.getName()})<#sep> + ", "
    </#list>
    + "};"
  </#if>
  + " scheduler type = " + this.scheduler.getClass().getSimpleName() + ";"
  + " oracle type = " + this.oracle.getClass().getSimpleName() + ";"
</@Log.info>
</#macro>
