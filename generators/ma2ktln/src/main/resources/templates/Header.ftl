<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/Comments.ftl" as Comment>
<#-- Prints parameterlists -->
<#-- @ftlvariable name="component" type="arcbasis._symboltable.ComponentTypeSymbol" -->
<#-- @ftlvariable name="util" type="TemplateUtilities" -->
<#macro ofClass>
    <@Comment.printOf node=component.getAstNode()/>
class ${component.getName()} (instanceName: String<#rt>
    <#list component.getParameters() as parameter>
        , private var ${parameter.getName()}Field :${util.getTypes().printType(parameter.getType())}<#t>
        <#if parameter.getAstNode().isPresentDefault()>
            = ${util.printExpression(parameter.getAstNode().getDefault())}<#t>
        </#if>
    </#list>
    ) : <#t>
    <#if component.isPresentParentComponent()>
        ${component.getParent().getTypeInfo().getName()}<#t>
    <#elseif component.isAtomic()>
      AComponent<#t>
    <#else>
      ADecomposedComponent<#t>
    </#if>
    <@listParentParameters/>
</#macro>
<#macro listParentParameters>
    (instanceName<#t>
    <#list component.getParameters()?filter(p -> component.isPresentParentComponent()) as parameter>
        , ${parameter.getName()}Field<#t>
    </#list>
    )<#t>
</#macro>