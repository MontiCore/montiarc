<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/MsgGuardInterface.ftl", [])}
${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/MsgActionInterface.ftl", [])}

protected ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}<@Util.printTypeParameters ast false/> states;

protected ${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(ast.getSymbol())}<#if isTop>${suffixes.top()}</#if> (
  ${ast.getName()}${suffixes.context()}<@Util.printTypeParameters ast false/> ${ast.getName()?uncap_first}${suffixes.context()},
  ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}<@Util.printTypeParameters ast false/> states,
  montiarc.rte.automaton.State initial, String name) {
    super(${ast.getName()?uncap_first}${suffixes.context()}, initial, name);
    this.states = states;
    <#-- Create transitions on tick -->
    this.transitions.addAll(java.util.Arrays.asList(
    <#list helper.getTransitionsWithoutEvent(helper.getAutomatonBehavior(ast).get()) as transition>
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [helper.getAutomatonBehavior(ast).get(), transition, false, true, ast.getSymbol().getAllIncomingPorts()])}<#sep >, </#sep>
    </#list>
    ));
}

${tc.includeArgs("montiarc/generator/ma2jsim/behavior/sync/UnsupportedEventBehaviorMembers.ftl", [true])}
