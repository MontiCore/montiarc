<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign modeAutomatonOpt = helper.getComponentHelper().getModeAutomaton(ast)/>

<#if !modeAutomatonOpt.isPresent()>
  protected void <@MethodNames.setupUnconnectedOutPorts/>() {
    this.unconnectedOutputs = java.util.Set.of(
    <#list helper.getComponentHelper().getUnconnectedOutPortsWithoutModes(ast.getSymbol()) as port>
      this.${prefixes.port()}${port.getName()}()
    <#sep>,
    </#list>
    );
  }
</#if>