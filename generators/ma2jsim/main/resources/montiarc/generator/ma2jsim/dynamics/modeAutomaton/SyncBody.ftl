<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign aut=helper.getModeAutomaton(ast).get()/>
public ${ast.getName()}${suffixes.modeAutomaton()}<#if isTop>TOP</#if> (
  ${ast.getName()}${suffixes.component()}<@Util.printTypeParameters ast false/> ${ast.getName()?uncap_first}${suffixes.component()}) {
  this(
    ${ast.getName()?uncap_first}${suffixes.component()},
    <@MethodNames.modes/>(),
    new java.util.ArrayList<>(),
    ${ast.getName()}${suffixes.modes()}.${prefixes.mode()}${helper.getInitialModes(aut)?first.getName()}
  );
  this.transitions.addAll(modeTransitions());
}

public ${ast.getName()}${suffixes.modeAutomaton()}<#if isTop>TOP</#if> (
  ${ast.getName()}${suffixes.component()}<@Util.printTypeParameters ast false/> ${ast.getName()?uncap_first}${suffixes.component()},
  java.util.List${"<"}montiarc.rte.automaton.State${">"} states,
  java.util.List${"<"}montiarc.rte.automaton.Transition${">"} transitions,
  montiarc.rte.automaton.State initial) {
    super(${ast.getName()?uncap_first}${suffixes.component()}, states, transitions, initial);
    this.context = ${ast.getName()?uncap_first}${suffixes.component()};
}

${tc.include("montiarc/generator/ma2jsim/dynamics/modeAutomaton/ModeListMethod.ftl")}

protected java.util.Collection${"<montiarc.rte.automaton.Transition>"} modeTransitions() {
  return java.util.List.of(
    <#list helper.getTransitions(aut) as transition>
        ${tc.includeArgs("montiarc/generator/ma2jsim/dynamics/modeAutomaton/TransitionBuilderCall.ftl", [transition, ast.getSymbol().getAllIncomingPorts()])}<#sep>, </#sep>
    </#list>
  );
}
