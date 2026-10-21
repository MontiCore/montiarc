<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

protected void <@MethodNames.portSetup/>() {
  ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

  <#list ast.getSymbol().getAllIncomingPorts() as port>
    this.${prefixes.port()}${port.getName()} = new montiarc.rte.port.ScheduledPort<>(getName() + ".${port.getName()}", this, scheduler);
  </#list>
  <#list ast.getSymbol().getAllOutgoingPorts() as port>
    this.${prefixes.port()}${port.getName()} = new montiarc.rte.port.PortForward<>(getName() + ".${port.getName()}", this);
  </#list>
}
