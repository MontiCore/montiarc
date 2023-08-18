<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign automaton = helper.getAutomatonBehavior(ast).get()/>
<#assign isEvent = helper.isEventBased(automaton)/>
<#assign MODIFIER><#if isTop>abstract<#else></#if></#assign>
<#assign CLASS>${ast.getName()}${suffixes.automaton()}${suffixes.builder()}<#if isTop>TOP</#if></#assign>
<#assign SUPER>montiarc.rte.automaton.<#if isEvent>Event<#else>Sync</#if>Automaton${suffixes.builder()}${"<"}${ast.getName()}${suffixes.context()}, ${ast.getName()}${suffixes.automaton()}${">"}</#assign>
<#assign CONTEXT>${ast.getName()}${suffixes.context()}</#assign>

public ${MODIFIER} class ${CLASS} extends ${SUPER} {

<#-- Constructors -->
public ${CLASS}(${CONTEXT} context,
    java.util.List${"<"}montiarc.rte.automaton.State${">"} states<#if !isEvent>,
    java.util.List${"<"}montiarc.rte.automaton.Transition${">"} transitions
  </#if>) {
  super(context, states<#if !isEvent>, transitions</#if>);
}

public ${CLASS}(${CONTEXT} context) { super(context); }

public ${CLASS}() { super(); }

<#-- Methods -->
@Override
public ${SUPER} addDefaultStates() {
  this.addStates(java.util.List.of(
    <#list helper.streamToList(automaton.streamStates()) as state>${ast.getName()}${suffixes.states()}.${prefixes.state()}${state.getName()}<#sep>, </#sep></#list>
  ));
  return this;
}

<#if !isEvent>
@Override
public ${SUPER} addDefaultTransitions() {
<#list helper.getTransitionsWithoutEvent(automaton) as transition>
  this.addTransition(${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [transition, ast.getSymbol().getAllIncomingPorts()])});
</#list>
  return this;
}
</#if>

@Override
public ${SUPER} setDefaultInitial() {
  this.setInitial(${ast.getName()}${suffixes.states()}.${prefixes.state()}${helper.streamToList(automaton.streamInitialStates())?first.getName()});
  return this;
}

<#if !isTop>
public ${ast.getName()}${suffixes.automaton()} buildActual(${ast.getName()}${suffixes.context()} context,
  java.util.List${"<"}montiarc.rte.automaton.State${">"} states,
  <#if !isEvent>java.util.List${"<"}montiarc.rte.automaton.Transition${">"} transitions,</#if>
  montiarc.rte.automaton.State initial) {
    return new ${ast.getName()}${suffixes.automaton()}(context, states, <#if !isEvent>transitions, </#if>initial);
}
</#if>
}
