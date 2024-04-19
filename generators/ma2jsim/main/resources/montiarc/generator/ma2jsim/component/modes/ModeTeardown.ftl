<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/component/modes/ModeUtil.ftl" as ModeUtil>

<#assign modeAutomaton = helper.getModeAutomaton(ast).get()>
<#assign modes = helper.getModes(modeAutomaton)>
<#list modes as mode>
  @Override
  public void <@MethodNames.modeTeardown mode.getSymbol()/>() {
    <@teardownConnectors mode/>
    <@teardownSubs mode/>
  }
</#list>

<#-- ASTArcMode mode -->
<#macro teardownConnectors mode>

  // Tear down connectors
  <#list helper.getConnectors(mode) as connector>
    <#assign sourcePort><@ModeUtil.calcPortAccessor connector.getSource() mode ast/></#assign>
    <#list connector.getTargetList() as target>
      <#assign targetPort><@ModeUtil.calcPortAccessor target mode ast/></#assign>
      ((montiarc.rte.port.IOutPort) ${sourcePort}).disconnect((montiarc.rte.port.IInPort) ${targetPort});
    </#list>
  </#list>
</#macro>

<#-- ASTArcMode mode -->
<#macro teardownSubs mode>

  // Tear down sub components
  <#list helper.getInstancesFromMode(mode) as sub>
    <#assign subSymbol = sub.getSymbol()>
  <#assign subCompName>this.${prefixes.subcomp()}${mode.getName()}_${subSymbol.getName()}${helper.subcomponentVariantSuffix(ast, subSymbol)}</#assign>
    ${subCompName} = null;
  </#list>
</#macro>