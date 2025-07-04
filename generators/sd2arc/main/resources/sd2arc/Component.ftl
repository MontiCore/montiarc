<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTSequenceDiagram ast -->
<<<#list helper.getStereotypes(ast) as key, value>${key}<#if value??>=${value}</#if><#sep>, </#sep></#list>>>
component ${ast.getName()} {
<#if helper.isEmbedded(ast)>
  // -- Embedding component start --
  <#list helper.getEmbeddingBody(ast) as element>
  ${arcPrinter.prettyprint(element)}
  </#list>
  // -- Embedding component end --
<#else>
  <#list helper.getVisibleComponents(ast) as subcomponent>
  ${prettyPrinter.prettyprint(subcomponent.getMCObjectType())} ${subcomponent.getName()}<#if subcomponent.isPresentArguments()>${prettyPrinter.prettyprint(subcomponent.getArguments())}</#if>;
  </#list>

  <#list helper.getImpliedConnectors(ast) as target, source>
  ${source} -> ${target};
  </#list>
</#if>

  ${tc.include("sd2arc.AssertOrderSubcomponent.ftl")}
}