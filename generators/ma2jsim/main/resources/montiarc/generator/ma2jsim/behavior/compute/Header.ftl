<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop", "compute")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign ubGenerics><@Util.printTypeParameters ast false/></#assign>
<#assign variantID = helper.variantSuffix(ast.getSymbol())>
<#assign CONTEXT = ast.getName() + suffixes.context() + ubGenerics>
<#assign SYNC_MSG = ast.getName() + suffixes.syncMsg() + ubGenerics>
<#assign EVENTS = ast.getName() + suffixes.events() + ubGenerics>

public <#if isTop>abstract</#if> class ${ast.getName()}${suffixes.compute()}${variantID}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>
extends montiarc.rte.behavior.AbstractBehavior${"<"} ${CONTEXT}, ${SYNC_MSG} ${">"}
implements ${EVENTS}