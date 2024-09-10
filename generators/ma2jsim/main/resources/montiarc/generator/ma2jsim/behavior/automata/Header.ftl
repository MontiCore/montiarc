<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("automaton")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign isEvent = helper.isEventBased(automaton)/>
<#assign CONTEXT> ${ast.getName()}${suffixes.context()} <@Util.printTypeParameters ast false/> </#assign>
<#assign SYNC_MSG> ${ast.getName()}${suffixes.syncMsg()} <@Util.printTypeParameters ast false/> </#assign>

public <#if isTop>abstract</#if> class ${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(ast.getSymbol())}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>
  extends <#if isEvent> <@eventParent/> <#else> <@syncParent/> </#if>
  implements ${ast.getName()}${suffixes.events()}<@Util.printTypeParameters ast false/>

<#macro eventParent>
  montiarc.rte.automaton.EventAutomaton${"<"} ${CONTEXT}, ${SYNC_MSG} ${">"}
</#macro>

<#macro syncParent>
  montiarc.rte.automaton.SyncAutomaton${"<"} ${CONTEXT}, ${SYNC_MSG} ${">"}
</#macro>

