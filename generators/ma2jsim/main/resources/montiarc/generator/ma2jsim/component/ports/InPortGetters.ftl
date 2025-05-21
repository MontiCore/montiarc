<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#list ast.getSymbol().getAllIncomingPorts() as portSym>
    ${tc.includeArgs("montiarc.generator.ma2jsim.component.ports.PortGetter.ftl", [portSym])}
</#list>