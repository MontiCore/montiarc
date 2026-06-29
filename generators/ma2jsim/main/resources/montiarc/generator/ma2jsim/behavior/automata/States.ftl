<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("variant")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign ubGenerics><@Util.printTypeParameters ast false/></#assign>
<#assign CONTEXT>${ast.getName()}${suffixes.context()}${ubGenerics}</#assign>
<#assign CLASS>${ast.getName()}${suffixes.states()}${helper.getVariantHelper().variantSuffix(variant)}<#if isTop>${suffixes.top()}</#if></#assign>
<#assign automaton = helper.getBehaviorHelper().getAutomatonBehavior(ast).get() />

@de.se_rwth.commons.Generated("montiarc.generators.ma2jsim")
public class ${CLASS}<@Util.printTypeParameters ast/> {
  protected ${CONTEXT} context;

  public ${CLASS}(${CONTEXT} context) {
    this.context = context;
  }

<#list automaton.getStates()?reverse as state>
  <#assign invariantOfSuperState = false>
  <#list automaton.getStates()?reverse as superstate>
    <#list helper.getBehaviorHelper().getSubstates(superstate) as substate>
      <#if "${state.getName()}" == "${substate.getName()}">
        <#if "${helper.getBehaviorHelper().getStateInvariant(superstate)}" != "">
          <#assign invariantOfSuperState = true>
        </#if>
      </#if>
    </#list>
  </#list>
  <#assign initialSubstateList = helper.getBehaviorHelper().getInitialSubstates(state)>
  public montiarc.rte.automaton.State ${prefixes.state()}${state.getName()} = new montiarc.rte.automaton.StateBuilder().setName("${state.getName()}")
  <#list helper.getBehaviorHelper().getSubstates(state)>
  .setSubstates(
    java.util.Arrays.asList(
      <#items as substate>
        state_${substate.getName()} <#sep >, </#sep>
      </#items>
    ))
  </#list>
  <#list helper.getBehaviorHelper().getInitialSubstates(state)>
  .setInitialSubstates(
    java.util.Arrays.asList(
      <#items as substate>
        state_${substate.getName()} <#sep >, </#sep>
      </#items>
    ))

  </#list>
  <#if helper.getBehaviorHelper().getEntryAction(state).isPresent()>
  .setEntryAction(
    (in) -> {
    <@executeAction> ${javaPrinter.generateCode(helper.getBehaviorHelper().getEntryAction(state).get())} </@executeAction>
    <#if "${helper.getBehaviorHelper().getStateInvariant(state)}" != "" || invariantOfSuperState>
      if(!${state.getName()?substring(0, 1)?lower_case + state.getName()?substring(1)}Invariant()) {
        de.se_rwth.commons.logging.Log.warn("Invariant not satisfied before entering state ${state.getName()} ");
      }
    </#if>
  })
  <#else>
    <#if "${helper.getBehaviorHelper().getStateInvariant(state)}" != "" || invariantOfSuperState>
    .setEntryAction(
      (in) -> {
      if(!${state.getName()?substring(0, 1)?lower_case + state.getName()?substring(1)}Invariant()) {
        de.se_rwth.commons.logging.Log.warn("Invariant not satisfied before entering state ${state.getName()} ");
      }
    })
    </#if>
  </#if>
  <#if helper.getBehaviorHelper().getExitAction(state).isPresent()>
  .setExitAction((in) -> {
    <@executeAction> ${javaPrinter.generateCode(helper.getBehaviorHelper().getExitAction(state).get())} </@executeAction>
  })
  </#if>
  <#if helper.getBehaviorHelper().getDoAction(state).isPresent()>
  .setDoAction((in) -> {
    <@executeAction> ${javaPrinter.generateCode(helper.getBehaviorHelper().getDoAction(state).get())} </@executeAction>
    <#if "${helper.getBehaviorHelper().getStateInvariant(state)}" != "" || invariantOfSuperState>
      if(!${state.getName()?substring(0, 1)?lower_case + state.getName()?substring(1)}Invariant()) {
        de.se_rwth.commons.logging.Log.warn("Invariant not satisfied after executing Do-Action of state ${state.getName()} ");
      }
    </#if>
  })
  <#else>
    <#if invariantOfSuperState>
    .setDoAction((in) -> {
      if(!${state.getName()?substring(0, 1)?lower_case + state.getName()?substring(1)}Invariant()) {
        de.se_rwth.commons.logging.Log.warn("Invariant not satisfied after executing Do-Action");
      }
    })
    </#if>
  </#if>
  .build();
</#list>

<#list automaton.getStates()?reverse as state>
  <#if "${helper.getBehaviorHelper().getStateInvariant(state)}" != "">
    public boolean ${state.getName()?substring(0, 1)?lower_case + state.getName()?substring(1)}Invariant() {
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
      ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getComponentHelper().getFeatures(ast)])}

      return ${helper.getBehaviorHelper().getStateInvariant(state)}
      <#list automaton.getStates()?reverse as superstate>
        <#list helper.getBehaviorHelper().getSubstates(superstate) as substate>
          <#if "${state.getName()}" == "${substate.getName()}">
            && ${superstate.getName()?substring(0, 1)?lower_case + superstate.getName()?substring(1)}Invariant()
          </#if>
        </#list>
      </#list>
      ;
    }
  <#else>
    <#list automaton.getStates()?reverse as superstate>
      <#list helper.getBehaviorHelper().getSubstates(superstate) as substate>
        <#if "${state.getName()}" == "${substate.getName()}">
          <#if "${helper.getBehaviorHelper().getStateInvariant(superstate)}" != "">
            public boolean ${state.getName()?substring(0, 1)?lower_case + state.getName()?substring(1)}Invariant() {
              ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
              ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
              ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getComponentHelper().getFeatures(ast)])}

              return ${superstate.getName()?substring(0, 1)?lower_case + superstate.getName()?substring(1)}Invariant();
            }
          </#if>
        </#if>
      </#list>
    </#list>
  </#if>
</#list>

}

<#macro executeAction>
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getComponentHelper().getFeatures(ast)])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
  <#nested> <#-- = Everything that goes inbetween the applied <@executeAction> ... </@executeAction> tags-->
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}
</#macro>
