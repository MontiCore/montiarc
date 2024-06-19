<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop", "variant")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign ubGenerics><@Util.printTypeParameters ast false/></#assign>
<#assign CONTEXT>${ast.getName()}${suffixes.context()}${ubGenerics}</#assign>
<#assign CLASS>${ast.getName()}${suffixes.states()}${helper.variantSuffix(variant)}<#if isTop>${suffixes.top()}</#if></#assign>
<#assign automaton = helper.getAutomatonBehavior(ast).get() />

public class ${CLASS}<@Util.printTypeParameters ast/> {
  protected ${CONTEXT} context;

  public ${CLASS}(${CONTEXT} context) {
    this.context = context;
  }

<#list automaton.getStates()?reverse as state>
  <#assign initialSubstateList = helper.getInitialSubstates(state)>
  public montiarc.rte.automaton.State ${prefixes.state()}${state.getName()} = new montiarc.rte.automaton.StateBuilder().setName("${state.getName()}")
  <#list helper.getSubstates(state)>
  .setSubstates(
    java.util.Arrays.asList(
      <#items as substate>
        state_${substate.getName()} <#sep >, </#sep>
      </#items>
    ))
  </#list>
  <#list helper.getInitialSubstates(state)>
  .setInitialSubstates(
    java.util.Arrays.asList(
      <#items as substate>
        state_${substate.getName()} <#sep >, </#sep>
      </#items>
    ))
  </#list>
  <#list helper.getInitAction(state)>
  .setInitAction(() -> {
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
    <#items as initStatement>
      ${prettyPrinter.prettyprint(initStatement)}
    </#items>
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}
  })
  </#list>
  <#if helper.getEntryAction(state).isPresent()>
  .setEntryAction(() -> {
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowInputs.ftl", [ast.getSymbol().getAllIncomingPorts(), true])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
    ${prettyPrinter.prettyprint(helper.getEntryAction(state).get())}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}
  })
  </#if>
  <#if helper.getExitAction(state).isPresent()>
  .setExitAction(() -> {
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowInputs.ftl", [ast.getSymbol().getAllIncomingPorts(), true])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
    ${prettyPrinter.prettyprint(helper.getExitAction(state).get())}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}
  })
  </#if>
  .build();
</#list>
}
