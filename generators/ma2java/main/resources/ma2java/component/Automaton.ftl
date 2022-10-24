<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp")}

<@printCurrentState/>

<@printCompute ast/>

<@printStateEnum ast/>

<#list autHelper.getAutomatonStates(ast) as state>

  <@printTransitionFrom state comp ast/>

  <@printTransitionTo state/>

  <@printEntry state comp/>

  <@printExit state comp/>

  <@printInitState state comp/>
</#list>

<@printInit ast/>

<#macro printStateEnum automaton>
  protected enum States {
    <#list autHelper.getAutomatonStates(automaton) as state>
      ${state.getName()} <#sep> , </#sep>
    </#list>
  }
</#macro>

<#macro printCompute automaton>
  public void compute() {
    // log state @ pre
    montiarc.rte.log.Log.trace(new StringBuilder()
      .append("State of comp '").append(this.getInstanceName())
      .append("' at pre = ")
      .append(this.get${identifier.getCurrentStateName()?cap_first}())
      .toString());
    switch (${identifier.getCurrentStateName()}) {
      <#list autHelper.getAutomatonStates(automaton) as state>
        case ${state.getName()}:
          transitionFrom${state.getName()}();
          break;
      </#list>
    }
    // log port values
    this.logPortValues();

    // log state @ post
    montiarc.rte.log.Log.trace(new StringBuilder()
      .append("State of comp '").append(this.getInstanceName())
      .append("' at post = ")
      .append(this.get${identifier.getCurrentStateName()?cap_first}())
      .toString());
  }
</#macro>

<#macro printCurrentState>
  protected States ${identifier.getCurrentStateName()};

  protected States get${identifier.getCurrentStateName()?cap_first}() {
    return this.${identifier.getCurrentStateName()};
  }
</#macro>

<#macro printTransitionFrom state comp automaton>
  protected void transitionFrom${state.getName()}() {
  // input
  <@printLocalInputVariables comp/>
  <#assign output><@printLocalOutputVariables comp/></#assign>
  <#assign result><@printSetOutput comp/></#assign>

  <#list autHelper.getAllTransitionsWithGuardFrom(automaton, state) as transition>
    ${tc.includeArgs("ma2java.component.Transition.ftl", transition,
      compHelper.asList(state, automaton, output, result))
    }
  </#list>
  <#if autHelper.hasTransitionWithoutGuardFrom(automaton, state)>
    ${tc.includeArgs("ma2java.component.Transition.ftl",
      autHelper.getFirstTransitionWithoutGuardFrom(automaton, state),
      compHelper.asList(state, automaton, output, result))
    }
  <#elseif autHelper.hasSuperState(automaton, state) && autHelper.isFinalState(automaton, state)>
    // exit state
    this.exit${state.getName()}();
    transitionFrom${autHelper.getSuperState(automaton, state).getName()}();
  </#if>
}
</#macro>

<#macro printTransitionTo state>
  protected void transitionTo${state.getName()}() {
    // transition to state
    this.${identifier.currentStateName} = States.${state.getName()};
    this.entry${state.getName()}();
    <#if autHelper.hasSubStates(state)>
      // transition to sub-state
      <#assign substate = autHelper.getInitialSubStatesStream(state).findFirst().get()>
      this.init${substate.getName()}();
      this.transitionTo${substate.getName()}();
    </#if>
  }
</#macro>

<#macro printEntry state comp>
  protected void entry${state.getName()}() {
    <#if autHelper.hasEntryAction(state)>
      // inputs
      <@printLocalInputVariables comp/>

      // outputs
      <@printLocalOutputVariables comp/>

      // entry action
      ${compHelper.printStatement(autHelper.getEntryActionBlockStatement(state))}

      // result
      <@printSetOutput comp/>
    </#if>
  }
</#macro>

<#macro printExit state comp>
  protected void exit${state.getName()}() {
    <#if autHelper.hasExitAction(state)>
      // inputs
      <@printLocalInputVariables comp/>

      // outputs
      <@printLocalOutputVariables comp/>

      // exit action
      ${compHelper.printStatement(autHelper.getExitActionBlockStatement(state))}

      // result
      <@printSetOutput comp/>
    </#if>
  }
</#macro>

<#macro printInitState state comp>
  protected void init${state.getName()}() {
    <#if autHelper.hasInitAction(state)>
      // inputs
      <@printLocalInputVariables comp/>

      // outputs
      <@printLocalOutputVariables comp/>

      // initial action
      <#list autHelper.getInitActionStatementList(state) as initAction>
        ${compHelper.printStatement(initAction)}
      </#list>

      // result
      <@printSetOutput comp/>
    </#if>
  }
</#macro>

<#macro printLocalInputVariables comp>
  <#list comp.getAllIncomingPorts() as port>
    final ${compHelper.getRealPortTypeString(port)} ${port.getName()} = this.get${port.getName()?cap_first}().getValue();
  </#list>
</#macro>

<#macro printLocalOutputVariables comp>
  <#list comp.getAllOutgoingPorts() as port>
    ${compHelper.getRealPortTypeString(port)} ${port.getName()} = this.get${port.getName()?cap_first}().getValue();
  </#list>
</#macro>

<#macro printSetOutput comp>
  <#list comp.getAllOutgoingPorts() as port>
    this.get${port.getName()?cap_first}().setValue(${port.getName()});
  </#list>
</#macro>

<#macro printInit automaton>
  @Override
  public void init() {
    <#assign state = automaton.streamInitialStates().findFirst().get()>
    // execute the initial action
    this.init${state.getName()}();
    // transition to the initial state
    this.transitionTo${state.getName()}();
  }
</#macro>
