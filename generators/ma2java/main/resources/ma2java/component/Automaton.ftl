<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp")}

<@printCurrentState/>

<@printCompute ast comp/>

<@printStateEnum ast/>

<@printExitStates ast/>

<#list autHelper.getAutomatonStates(ast) as state>

  <@printTransitionFrom state comp ast/>

  <@printTransitionTo state/>

  <@printEntry state comp/>

  <@printExit state comp/>

  <@printInitState state comp/>

</#list>

<@printInit ast comp/>

<#macro printStateEnum automaton>
  protected enum States {
    <#list autHelper.getAutomatonStates(automaton) as state>
      ${state.getName()}(<#if autHelper.hasSuperState(automaton, state)>${autHelper.getSuperState(automaton, state).getName()}</#if>)<#sep> , </#sep>
    </#list>;

    final States superState;

    java.util.Optional<States> getSuperState() {
      return java.util.Optional.ofNullable(this.superState);
    }

    States() {
      this.superState = null;
    }

    States(States superState) {
      this.superState = superState;
    }
  }
</#macro>

<#macro printCompute automaton comp>
  public void compute() {
    montiarc.rte.log.Log.comment("Computing component " + this.getInstanceName() + "");
    // log state @ pre
    montiarc.rte.log.Log.trace(
      "State@pre = "
      + this.get${identifier.getCurrentStateName()?cap_first}()
    );
    // log input values
    <#list comp.getIncomingPorts() as port>
      montiarc.rte.log.Log.trace(
        "Value of input port ${port.getName()} = "
        + this.get${port.getName()?cap_first}().getValue()
      );
    </#list>
    // transition from the current state
    switch (${identifier.getCurrentStateName()}) {
      <#list autHelper.getAutomatonStates(automaton) as state>
        case ${state.getName()}:
          transitionFrom${state.getName()}();
          break;
      </#list>
    }
    // log output values
    <#list comp.getOutgoingPorts() as port>
      montiarc.rte.log.Log.trace(
        "Value of output port ${port.getName()} = "
        + this.get${port.getName()?cap_first}().getValue()
      );
    </#list>
    // log state @ post
    montiarc.rte.log.Log.trace(
      "State@post = "
      + this.get${identifier.getCurrentStateName()?cap_first}()
    );
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
  <#assign transitions = autHelper.getAllTransitionsWithGuardFrom(automaton, state)/>

  <#list transitions>
    <#items as transition>
    ${tc.includeArgs("ma2java.component.Transition.ftl", transition,
      compHelper.asList(state, automaton, output, result))
    }
    <#sep> else </#sep>
    </#items>
    else {
  </#list>
  <#if autHelper.hasTransitionWithoutGuardFrom(automaton, state)>
    ${tc.includeArgs("ma2java.component.Transition.ftl",
      autHelper.getFirstTransitionWithoutGuardFrom(automaton, state),
      compHelper.asList(state, automaton, output, result))
    }
  <#elseif autHelper.hasSuperState(automaton, state) && autHelper.isFinalState(automaton, state)>
    // transition from super state
    transitionFrom${autHelper.getSuperState(automaton, state).getName()}();
  </#if>
  <#if transitions?size != 0>}</#if>

  <@printSynchronize comp/>
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
    final <@printType port.getType()/> ${port.getName()} = this.get${port.getName()?cap_first}().getValue();
  </#list>
</#macro>

<#macro printLocalOutputVariables comp>
  <#list comp.getAllOutgoingPorts() as port>
    <@printType port.getType()/> ${port.getName()} = <@printDefaultValue port.getType()/>;
  </#list>
</#macro>

<#macro printSetOutput comp>
  <#list comp.getAllOutgoingPorts() as port>
    <#if !port.getType().isPrimitive()>if (${port.getName()} != null) </#if>this.get${port.getName()?cap_first}().setValue(${port.getName()});
  </#list>
</#macro>

<#macro printType type>
  <#if type.isPrimitive() || type.isTypeVariable()>${type.print()}<#else>${type.printFullName()}</#if>
</#macro>

<#macro printSynchronize comp>
  <#list comp.getAllOutgoingPorts() as port>
    this.get${port.getName()?cap_first}().sync();
  </#list>
</#macro>

<#macro printInit automaton comp>
  @Override
  public void init() {
    <#assign state = automaton.streamInitialOuterStates().findFirst().get()>
    // execute the initial action
    this.init${state.getName()}();
    // transition to the initial state
    this.transitionTo${state.getName()}();
    // provide initial value for delay ports
    <#list comp.getOutgoingPorts() as port>
      <#if port.isDelayed()>this.${port.getName()}.tick();</#if>
    </#list>
  }
</#macro>

<#macro printExitStates automaton>
  protected void exit(States from, States to) {
    switch (from) {
      <#list autHelper.getAutomatonStates(automaton) as state>
        case ${state.getName()} :
          exit${state.getName()}();
          break;
      </#list>
    }
    if (from != to && from.getSuperState().isPresent()) {
      exit(from.getSuperState().get(), to);
    }
  }
</#macro>

<#macro printDefaultValue portType><#if !portType.isPrimitive()>null<#elseif portType.print()?matches("boolean")>false<#else>0</#if></#macro>
