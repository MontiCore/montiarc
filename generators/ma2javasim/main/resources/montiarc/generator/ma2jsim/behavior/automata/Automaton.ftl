<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign automaton = helper.getAutomatonBehavior(ast).get()/>
<#assign isEvent = helper.isEventBased(automaton)/>
<#assign inTimed = helper.isComponentInputTimeAware(ast)/>
<#assign outTimed = helper.isComponentOutputTimeAware(ast)/>

${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.Header.ftl", ast, helper.asList(isTop))}
{
<#if isEvent>
  ${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.EventBody.ftl", ast, helper.asList(isTop))}
<#else>
  ${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.SyncBody.ftl", ast, helper.asList(isTop))}
</#if>
  ${tc.include("montiarc/generator/ma2jsim/behavior/automata/Init.ftl")}
}
