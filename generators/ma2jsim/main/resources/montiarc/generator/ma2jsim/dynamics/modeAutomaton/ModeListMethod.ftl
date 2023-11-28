<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
protected static java.util.List${"<montiarc.rte.automaton.State>"} <@MethodNames.modes/>() {
  return java.util.List.of(
    <#list helper.getModes(helper.getModeAutomaton(ast).get()) as mode>
        ${ast.getName()}${suffixes.modes()}.${prefixes.mode()}${mode.getName()}<#sep>, </#sep>
    </#list>
  );
}
