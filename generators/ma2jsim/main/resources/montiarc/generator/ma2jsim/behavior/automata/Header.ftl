<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("automaton")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign CONTEXT> ${ast.getName()}${suffixes.context()} <@Util.printTypeParameters ast false/> </#assign>
<#assign SYNC_MSG> ${ast.getName()}${suffixes.syncMsg()} <@Util.printTypeParameters ast false/> </#assign>

@de.se_rwth.commons.Generated("montiarc.generators.ma2jsim")
public <#if isTop>abstract</#if> class ${ast.getName()}${suffixes.automaton()}${helper.getVariantHelper().variantSuffix(ast.getSymbol())}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>
  extends montiarc.rte.automaton.Automaton${"<"} ${CONTEXT}, ${SYNC_MSG} ${">"}
  implements ${ast.getName()}${suffixes.events()}<@Util.printTypeParameters ast false/>
