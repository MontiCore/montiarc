<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("variant")}
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
  .setInitAction((in) -> {
    <@executeAction>
      <#items as initStatement>
        ${prettyPrinter.prettyprint(initStatement)}
      </#items>
    </@executeAction>
  })
  </#list>
  <#if helper.getEntryAction(state).isPresent()>
  .setEntryAction((in) -> {
    <@executeAction> ${prettyPrinter.prettyprint(helper.getEntryAction(state).get())} </@executeAction>
  })
  </#if>
  <#if helper.getExitAction(state).isPresent()>
  .setExitAction((in) -> {
    <@executeAction> ${prettyPrinter.prettyprint(helper.getExitAction(state).get())} </@executeAction>
  })
  </#if>
  <#if helper.getDoAction(state).isPresent()>
  .setDoAction((in) -> {
    <@executeAction> ${prettyPrinter.prettyprint(helper.getDoAction(state).get())} </@executeAction>
  })
  </#if>
  .build();
</#list>
}

<#macro executeAction>
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
  <#nested> <#-- = Everything that goes inbetween the applied <@executeAction> ... </@executeAction> tags-->
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}
</#macro>
