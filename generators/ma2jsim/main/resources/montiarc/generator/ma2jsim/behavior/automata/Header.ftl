<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop", "automaton")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign isEvent = helper.isEventBased(automaton)/>
public <#if isTop>abstract</#if> class ${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(ast.getSymbol())}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/> extends
  montiarc.rte.automaton.<#if isEvent>Event<#else>Sync</#if>Automaton${"<"}${ast.getName()}${suffixes.context()}<@Util.printTypeParameters ast false/>${">"} <#if isEvent>implements ${ast.getName()}${suffixes.events()}</#if>