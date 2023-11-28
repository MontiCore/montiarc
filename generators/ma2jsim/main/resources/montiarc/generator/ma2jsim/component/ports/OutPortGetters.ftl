<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#list ast.getSymbol().getAllOutgoingPorts() as portSym>
    ${tc.includeArgs("montiarc.generator.ma2jsim.component.ports.PortGetter.ftl", [portSym, ast.getSymbol().isAtomic()])}
</#list>