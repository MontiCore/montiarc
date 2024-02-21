<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#assign atomic = ast.getSymbol().isAtomic()>
${tc.include("montiarc.generator.ma2jsim.component.ports.InPortGetters.ftl")}
${tc.include("montiarc.generator.ma2jsim.component.ports.OutPortGetters.ftl")}
<#list ast.getSymbol().getAllPorts() as portSym>
  ${tc.includeArgs("montiarc.generator.ma2jsim.component.ports.PortField.ftl", [portSym])}
</#list>
${tc.include("montiarc.generator.ma2jsim.component.ports.PortSetup.ftl")}
${tc.include("montiarc.generator.ma2jsim.component.ports.PortAllGetter.ftl")}
