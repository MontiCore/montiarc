<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("variant")}

<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign automaton = helper.getAutomatonBehavior(ast).get() />
<#assign ubGenerics><@Util.printTypeParameters ast false/></#assign>
<#assign isEvent = helper.isEventBased(automaton)/>
<#assign MODIFIER><#if isTop>abstract</#if></#assign>
<#assign CLASS>${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(ast.getSymbol())}${suffixes.builder()}<#if isTop>TOP</#if></#assign>
<#assign CONTEXT>${ast.getName()}${suffixes.context()}${ubGenerics}</#assign>
<#assign SYNC_MSG>${ast.getName()}${suffixes.syncMsg()}${ubGenerics}</#assign>
<#assign BEHAVIOR>${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(ast.getSymbol())}${ubGenerics}</#assign>
<#assign SUPER>montiarc.rte.automaton.<#if isEvent>Event<#else>Sync</#if>Automaton${suffixes.builder()}${"<"} ${CONTEXT}, ${SYNC_MSG}, ${BEHAVIOR} ${">"}</#assign>

public ${MODIFIER} class ${CLASS}<@Util.printTypeParameters ast/> extends ${SUPER} {

<#-- Constructor -->
public ${CLASS}(${CONTEXT} context) { super(context); }

protected ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())} states;

<#-- Methods -->
@Override
public ${SUPER} addDefaultStates() {
  states = new ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())}(context);
  return this;
}

<#assign initSubstate= automaton.listInitialStates()>
@Override
public ${SUPER} setDefaultInitial() {
  this.setInitial(states.${prefixes.state()}${automaton.listInitialStates()?first.getName()});
  return this;
}

<#if !isTop>
public ${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(ast.getSymbol())}${ubGenerics} buildActual(${ast.getName()}${suffixes.context()}${ubGenerics} context,
  ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())} states,
  montiarc.rte.automaton.State initial, String name) {
    return new ${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(ast.getSymbol())}${ubGenerics}(context, states, initial, name);
}
<#else>
public abstract ${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(ast.getSymbol())}${ubGenerics} buildActual(${ast.getName()}${suffixes.context()}${ubGenerics} context,
  ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())} states,
  montiarc.rte.automaton.State initial, String name);
</#if>

public ${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(ast.getSymbol())}${ubGenerics} buildActual(${ast.getName()}${suffixes.context()}${ubGenerics} context,
montiarc.rte.automaton.State initial, String name) {
    return buildActual(context, states, initial, name);
  }
}
