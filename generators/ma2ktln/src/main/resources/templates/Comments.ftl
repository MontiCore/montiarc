<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Prints comments of a node -->
<#-- @ftlvariable name="node" type="de.monticore.ast.ASTNode" -->
<#macro commentedOut node>
  <#if !node.isEmpty_PreComments()>
    /**
    <#list node.get_PreCommentList() as comment>
        * ${comment.getText()}
    </#list>
     */
  </#if>
  <#list node.get_PostCommentList() as comment>
    // ${comment.getText()}
  </#list>
</#macro>
<#macro printOf node>
    <#list node.get_PreCommentList() as comment>
        ${comment.getText()}
    </#list>
    <#list node.get_PostCommentList() as comment>
        ${comment.getText()}
    </#list>
</#macro>