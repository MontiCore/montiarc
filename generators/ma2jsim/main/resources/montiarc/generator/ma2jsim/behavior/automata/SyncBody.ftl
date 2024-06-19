<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

protected ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())} states;

protected ${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(ast.getSymbol())}<#if isTop>${suffixes.top()}</#if> (
  ${ast.getName()}${suffixes.context()}<@Util.printTypeParameters ast false/> ${ast.getName()?uncap_first}${suffixes.context()},
  ${ast.getName()}${suffixes.states()}${helper.variantSuffix(ast.getSymbol())} states,
  montiarc.rte.automaton.State initial) {
    super(${ast.getName()?uncap_first}${suffixes.context()}, initial);
    this.states = states;
    <#-- Create transitions on tick -->
    this.transitions.addAll(java.util.Arrays.asList(
    <#list helper.getTransitionsWithoutEvent(helper.getAutomatonBehavior(ast).get()) as transition>
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/automata/TransitionBuilderCall.ftl", [helper.getAutomatonBehavior(ast).get(), transition, ast.getSymbol().getAllIncomingPorts()])}<#sep >, </#sep>
    </#list>
    ));
}

protected void executeDefaultAction() {
  ${tc.include("montiarc.generator.ma2jsim.behavior.automata.ShadowConstantsWithContext.ftl")}

  // Poll the first message from each incoming port (if it is not a tick)
  <#list ast.getSymbol().getAllIncomingPorts() as inPort>
    <#assign existenceConditions = helper.getExistenceCondition(ast, inPort)/>
    <#assign portGetterName>${prefixes.port()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}</#assign>

    if (<#if existenceConditions?has_content>${prettyPrinter.prettyprint(existenceConditions)}<#else>true</#if>) {
      <#assign current_port>getContext().${prefixes.port()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}()</#assign>
      if (!${current_port}.isTickBlocked()) { ${current_port}.pollBuffer(); }
    }
  </#list>
}