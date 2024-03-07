<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#list ast.getSymbol().getAllIncomingPorts() as portSym>
    ${tc.includeArgs("montiarc.generator.ma2jsim.component.ports.PortGetter.ftl", [portSym])}
</#list>