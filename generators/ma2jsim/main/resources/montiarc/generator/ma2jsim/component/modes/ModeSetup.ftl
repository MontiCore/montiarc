<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/component/modes/ModeUtil.ftl" as ModeUtil>

<#assign modeAutomaton = helper.getModeAutomaton(ast).get()>
<#assign modes = helper.getModes(modeAutomaton)>
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
  <#list helper.getInstancesFromMode(mode) as sub>
    <#assign subSymbol = sub.getSymbol()>
    <#assign subCompName>this.${prefixes.subcomp()}${mode.getName()}_${subSymbol.getName()}${helper.subcomponentVariantSuffix(ast, subSymbol)}</#assign>
    <#assign builderType><@Util.getCompTypeString subSymbol.getType() "${suffixes.component()}${suffixes.builder()}"/></#assign>

    ${subCompName} = new ${builderType}()
    .setName("${subSymbol.getName()}")
    .setScheduler(this.getScheduler())
      <#list helper.getArgNamesMappedToExpressions(subSymbol.getAstNode()) as name, expression>
        .${prefixes.setterMethod()}${prefixes.parameter()}${name}(${prettyPrinter.prettyprint(expression)})
      </#list>
    .build();
  </#list>
</#macro>

<#-- ASTArcMode mode -->
<#macro createConnectors mode>

  // Set up connectors
  <#list helper.getConnectors(mode) as connector>
    <#assign sourcePort><@ModeUtil.calcPortAccessor connector.getSource() mode ast/></#assign>
    <#list connector.getTargetList() as target>
      <#assign targetPort><@ModeUtil.calcPortAccessor target mode ast/></#assign>
      ((montiarc.rte.port.IOutPort) ${sourcePort}).connect((montiarc.rte.port.IInPort) ${targetPort});
    </#list>
  </#list>

  <#-- Also setup simulator-specific tick connectors -->
  <#list helper.getInstanceSymbolsFromMode(mode) as subComp>
    ((montiarc.rte.port.IOutPort) this.getTickPort()).connect(${prefixes.subcomp()}${mode.getName()}_${subComp.getName()}().getTickPort());
  </#list>
</#macro>

<#-- ASTArcMode mode, ASTComponentType compAst -->
<#macro updateUnconnectedOutputs mode compAst>
  this.unconnectedOutputs = java.util.Set.of(
    <#list helper.getUnconnectedOutPortsIncludingMode(compAst.getSymbol(), mode) as port>
      ${prefixes.port()}${port.getName()}${helper.portVariantSuffix(compAst, port)}()
    <#sep>,
    </#list>
  );
</#macro>