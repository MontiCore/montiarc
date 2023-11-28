<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign automaton = helper.getModeAutomaton(ast).get()/>
abstract class ${ast.getName()}${suffixes.modes()}<#if isTop>${suffixes.top()}</#if> {
<#list helper.getModes(automaton) as mode>
    ${tc.includeArgs("montiarc/generator/ma2jsim/dynamics/modeAutomaton/Mode.ftl", [mode])}
</#list>
}