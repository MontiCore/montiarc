<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/component/modes/ModeUtil.ftl" as ModeUtil>
<#import "/montiarc/generator/ma2jsim/logging/CompLogging.ftl" as Log>

<#assign modeAutomaton = helper.getComponentHelper().getModeAutomaton(ast).get()>
<#assign modes = helper.getModeHelper().getModes(modeAutomaton)>
<#list modes as mode>
  @Override
  public void <@MethodNames.modeSetup mode.getSymbol()/>() {
    <@createSubs mode/>
    <@createConnectors mode/>

    // Update other properties
    <@updateUnconnectedOutputs mode ast/>
  }
</#list>

<#-- ASTArcMode mode -->
<#macro createSubs mode>

  // Set up sub components
  <#list helper.getModeHelper().getInstancesFromMode(mode) as sub>
    <#assign subSymbol = sub.getSymbol()>
    <#assign subCompName>this.${prefixes.subcomp()}${mode.getName()}_${subSymbol.getName()}${helper.getVariantHelper().subcomponentVariantSuffix(ast, subSymbol)}</#assign>
    <#assign subCompType><@Util.getCompTypeString subSymbol.getType() "${suffixes.compImpl()}"/></#assign>
    <#assign builderType><@Util.getCompTypeString subSymbol.getType() "${suffixes.comp()}${suffixes.builder()}"/></#assign>

    ${subCompName} = (${subCompType}) new ${builderType}()
    .setName(this.getName() + ".${mode.getName()}." + "${subSymbol.getName()}")
    .setOracleFactory(oracleFactory)
    .setScheduler(this.getScheduler())
    .setSuperComponent(this)
      <#list helper.getComponentHelper().getArgNamesMappedToExpressions(subSymbol.getAstNode()) as name, expression>
        .${prefixes.setterMethod()}${prefixes.parameter()}${name}(${javaPrinter.generateCode(expression)})
      </#list>
    .build();
    <#-- In opposition to the mode teardown, we do not need to log the creation of new sub components,
      -- as their constructor already does so. -->
  </#list>
</#macro>

<#-- ASTArcMode mode -->
<#macro createConnectors mode>

  // Set up connectors
  <#list helper.getModeHelper().getConnectors(mode) as connector>
    <#assign sourcePort><@ModeUtil.calcPortAccessor connector.getSource() mode ast/></#assign>
    <#list connector.getTargetList() as target>
      <#assign targetPort><@ModeUtil.calcPortAccessor target mode ast/></#assign>
      ${sourcePort}.connect(${targetPort});
      <@Log.trace log_aspects.createConnector() "getName()">"${connector.getSourceName()} -> ${target.getQName()}"</@Log.trace>
    </#list>
  </#list>
</#macro>

<#-- ASTArcMode mode, ASTArcComponentType compAst -->
<#macro updateUnconnectedOutputs mode compAst>
  this.unconnectedOutputs = java.util.Set.of(
    <#list helper.getComponentHelper().getUnconnectedOutPortsIncludingMode(compAst.getSymbol(), mode) as port>
      ${prefixes.port()}${port.getName()}${helper.getVariantHelper().portVariantSuffix(compAst, port)}()
    <#sep>,
    </#list>
  );
</#macro>
