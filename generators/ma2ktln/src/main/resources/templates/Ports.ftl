<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Helper for port-related prints -->
<#-- @ftlvariable name="component" type="arcbasis._symboltable.ComponentTypeSymbol" -->
<#-- @ftlvariable name="util" type="TemplateUtilities" -->
<#-- @ftlvariable name="port" type="arcbasis._ast.ASTPortAccess" -->
<#macro printAccess port>
    <#if port.isPresentComponent()>
        getSubcomponent("${port.getComponent()}").<#t>
    </#if>
    get<#t>
    <#if port.getPortSymbol().isIncoming()>
        Input<#t>
    <#else>
        Output<#t>
    </#if>
    Port("${port.getPort()}")<#t>
</#macro>