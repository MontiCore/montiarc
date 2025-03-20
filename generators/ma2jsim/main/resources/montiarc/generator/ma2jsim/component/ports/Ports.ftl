<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.include("montiarc.generator.ma2jsim.component.ports.InPortGetters.ftl")}
${tc.include("montiarc.generator.ma2jsim.component.ports.OutPortGetters.ftl")}
<#list ast.getSymbol().getAllPorts() as portSym>
  ${tc.includeArgs("montiarc.generator.ma2jsim.component.ports.PortField.ftl", [portSym])}
</#list>
${tc.include("montiarc.generator.ma2jsim.component.ports.PortSetup.ftl")}
${tc.include("montiarc.generator.ma2jsim.component.ports.UnconnectedOutPortSetup.ftl")}
${tc.include("montiarc.generator.ma2jsim.component.ports.PortAllGetter.ftl")}
