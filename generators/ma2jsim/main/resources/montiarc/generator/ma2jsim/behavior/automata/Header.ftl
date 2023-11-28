<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign isEvent = helper.isEventBased(helper.getAutomatonBehavior(ast).get())/>
public <#if isTop>abstract</#if> class ${ast.getName()}${suffixes.automaton()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/> extends
  montiarc.rte.automaton.<#if isEvent>Event<#else>Sync</#if>Automaton${"<"}${ast.getName()}${suffixes.context()}<@Util.printTypeParameters ast false/>${">"}