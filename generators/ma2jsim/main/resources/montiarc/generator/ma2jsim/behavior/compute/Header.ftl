<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("compute")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign ubGenerics><@Util.printTypeParameters ast false/></#assign>
<#assign variantID = helper.getVariantHelper().variantSuffix(ast.getSymbol())>
<#assign CONTEXT = ast.getName() + suffixes.context() + ubGenerics>
<#assign SYNC_MSG = ast.getName() + suffixes.syncMsg() + ubGenerics>
<#assign EVENTS = ast.getName() + suffixes.events() + ubGenerics>

@de.se_rwth.commons.Generated("montiarc.generators.ma2jsim")
public <#if isTop>abstract</#if> class ${ast.getName()}${suffixes.compute()}${variantID}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>
extends montiarc.rte.behavior.AbstractBehavior${"<"} ${CONTEXT}, ${SYNC_MSG} ${">"}
implements ${EVENTS}
