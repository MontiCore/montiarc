<#-- (c) https://github.com/MontiCore/monticore -->
<#-- This template creates automaton implementations for components that have automatons. -->

<#import "/templates/util/Utils.ftl" as Utils>

<#macro printImplementationClassMethods comp compHelper identifier isTOPClass=false>
  <#assign compName = comp.getName()>
  <#assign automaton = compHelper.getAutomatonBehavior().get()>
  <#assign automatonHelper = compHelper.automatonHelperFrom(automaton)>

  // component config parameters
  <@Utils.printConfigParameters comp=comp/>

  // component variables
  <@Utils.printVariables comp=comp compHelper=compHelper/>

  // current state
  <@Utils.printMember visibility="protected" type=compName+"State" name=identifier.getCurrentStateName()/>

  <@printAutomatonStateEnum comp=comp compHelper=compHelper automatonHelper=automatonHelper/>

  <@printConstructor comp=comp isTOPClass=isTOPClass/>

  <@printGetInitialValues comp=comp compHelper=compHelper automatonHelper=automatonHelper identifier=identifier/>

  <@printCompute comp=comp compHelper=compHelper automatonHelper=automatonHelper identifier=identifier/>

  <#list automatonHelper.getAutomatonStates() as state>
    <@printAllStateMethods comp=comp compHelper=compHelper identifier=identifier automatonHelper=automatonHelper state=state/>
  </#list>
</#macro>

<#macro printConstructor comp isTOPClass=false>
  <#assign compName=comp.getName()>
  <#if isTOPClass>
    public ${compName}ImplTOP
  <#else>
    public ${compName}Impl
  </#if>
  ( <@Utils.printConfigurationParametersAsList comp=comp/> ) {

    <#list comp.getParameters() as param>
      this.${param.getName()} = ${param.getName()};
    </#list>

    <@Utils.printVariablesConstructor comp=comp compHelper=compHelper/>
  }
</#macro>

<#macro printAutomatonStateEnum comp compHelper automatonHelper>
  <#assign compName=comp.getName()>
  <#assign automaton = compHelper.getAutomatonBehavior().get()>
  protected static enum ${compName}State {
    <#list automatonHelper.getAutomatonStates() as state>
      ${state.getName()} <#sep> , </#sep>
    </#list>
  }
</#macro>

<#macro printGetInitialValues comp compHelper automatonHelper identifier>
  <#assign compName=comp.getName()>
  <#assign compTypeParams> <@Utils.printFormalTypeParameters comp=comp/> </#assign>
  <#assign resultClass>${comp.getName()}Result${compTypeParams}</#assign>
  <#assign resultVarName = identifier.getResultName()>
  <#assign automaton = compHelper.getAutomatonBehavior().get()>
  <#assign optInitialState = automaton.streamInitialStates().findFirst()>

  @Override
  public ${compName}Result ${compTypeParams} getInitialValues() {
    // Transition to initial state with initial action
    return <@transitionToMethodName state=optInitialState.get()/>(true).apply(new <@Utils.componentInputClassFQN comp=comp/>(), new ${resultClass}());
  }
</#macro>

<#macro transitionFromMethodName state>transitionFromState${state.getName()}</#macro>

<#macro transitionToMethodName state>transitionToState${state.getName()}</#macro>
<#macro transitionToMethodNameFromTransition transition>transitionToState${transition.targetName}</#macro>

<#macro entryMethodName state>state${state.getName()}EntryAction</#macro>

<#macro entryMethodNameFromString stateName>state${stateName}EntryAction</#macro>

<#macro exitMethodName state>state${state.getName()}ExitAction</#macro>

<#macro exitMethodNameFromString stateName>state${stateName}ExitAction</#macro>

<#macro initMethodName state>state${state.getName()}InitAction</#macro>

<#macro printCompute comp compHelper automatonHelper identifier>
<#assign inputParamName = identifier.getInputName()>
<#assign compTypeParams><@Utils.printFormalTypeParameters comp=comp/></#assign>
<#assign inputParam = identifier.getInputName()>
  public ${comp.getName()}Result ${compTypeParams} compute(${comp.getName()}Input ${compTypeParams} ${inputParam}) {
    switch (${identifier.getCurrentStateName()}) {
      <#list automatonHelper.getAutomatonStates() as state>
        case ${state.name}:
          return <@transitionFromMethodName state=state/>(${inputParam});
      </#list>
    }
    return new ${comp.getName()}Result ${compTypeParams}();
  }
</#macro>

<#macro printLocalVariablesFromInput comp compHelper identifier>
  <#assign inputParam = identifier.getInputName()>
  <#list comp.getAllIncomingPorts() as port>
    final ${compHelper.getRealPortTypeString(port)} ${port.getName()} = ${inputParam}.get${port.getName()?cap_first}();
  </#list>
</#macro>

<#macro printLocalVariablesFromResult comp compHelper identifier>
    <#assign resultParam = identifier.getResultName()>
    <#list comp.getAllOutgoingPorts() as port>
      ${compHelper.getRealPortTypeString(port)} ${port.getName()} = ${resultParam}.get${port.getName()?cap_first}();
    </#list>
</#macro>

<#macro printLocalVariablesForNewResult comp compHelper identifier>
    <#assign resultParam = identifier.getResultName()>
    <#list comp.getAllOutgoingPorts() as port>
        ${compHelper.getRealPortTypeString(port)} ${port.getName()} = null;
    </#list>
</#macro>

<#macro printStateEntryMethod comp compHelper automatonHelper identifier state>
  <#assign compTypeParams><@Utils.printFormalTypeParameters comp=comp/></#assign>
  <#assign resultClass>${comp.getName()}Result${compTypeParams}</#assign>
  <#assign resultParam = identifier.getResultName()>
  <#assign inputClass>${comp.getName()}Input${compTypeParams}</#assign>
  <#assign inputParam = identifier.getInputName()>
  protected ${resultClass} <@entryMethodName state=state/>(${inputClass} ${inputParam}, ${resultClass} ${resultParam}) {
    <#if automatonHelper.hasEntryAction(state)>
      // working copies of inputs - required to easily use the entry action block statement
      <@printLocalVariablesFromInput comp=comp compHelper=compHelper identifier=identifier/>

      // working copies of current outputs - required to easily use the entry action block statement
      <@printLocalVariablesFromResult comp=comp compHelper=compHelper identifier=identifier/>

      // entry action from the model
      ${compHelper.printStatement(automatonHelper.getEntryActionBlockStatement(state))}

      ${resultClass} newResult = new ${resultClass}();
      <#list comp.getAllOutgoingPorts() as port>
        newResult.set${port.getName()?cap_first}(${port.getName()});
      </#list>
      return newResult;
    <#else>
      //state has no entry action
      return ${resultParam};
    </#if>
  }
</#macro>

<#macro printStateExitMethod comp compHelper automatonHelper identifier state>
  <#assign compTypeParams><@Utils.printFormalTypeParameters comp=comp/></#assign>
  <#assign resultClass>${comp.getName()}Result${compTypeParams}</#assign>
  <#assign inputClass>${comp.getName()}Input${compTypeParams}</#assign>
  <#assign inputParam = identifier.getInputName()>
  protected ${resultClass} <@exitMethodName state=state/>(${inputClass} ${inputParam}) {
    <#if automatonHelper.hasExitAction(state)>
      // working copies of inputs - required to easily use the exit action block statement
      <@printLocalVariablesFromInput comp=comp compHelper=compHelper identifier=identifier/>

      // variables for outputs - required to easily use the exit action block statement
      <@printLocalVariablesForNewResult comp=comp compHelper=compHelper identifier=identifier/>

      // exit action from the model
      ${compHelper.printStatement(automatonHelper.getExitActionBlockStatement(state))}

      ${resultClass} newResult = new ${resultClass}();
        <#list comp.getAllOutgoingPorts() as port>
          newResult.set${port.getName()?cap_first}(${port.getName()});
        </#list>
      return newResult;
    <#else>
      // state has no exit action
      return new ${resultClass}();
    </#if>
  }
</#macro>

<#macro printStateInitMethod comp compHelper automatonHelper identifier state>
  <#assign compTypeParams><@Utils.printFormalTypeParameters comp=comp/></#assign>
  <#assign resultClass>${comp.getName()}Result${compTypeParams}</#assign>
  <#assign resultParam = identifier.getResultName()>
  <#assign inputClass>${comp.getName()}Input${compTypeParams}</#assign>
  <#assign inputParam = identifier.getInputName()>
  protected ${resultClass} <@initMethodName state=state/>(${inputClass} ${inputParam}, ${resultClass} ${resultParam}) {
    <#if automatonHelper.hasInitAction(state)>
      // working copies of inputs - required to easily use the entry action block statement
      <@printLocalVariablesFromInput comp=comp compHelper=compHelper identifier=identifier/>

      // working copies of current outputs - required to easily use the entry action block statement
      <@printLocalVariablesFromResult comp=comp compHelper=compHelper identifier=identifier/>

      // initial action from the model
      <#assign initialActions = automatonHelper.getInitActionStatementList(state)>
      <#list initialActions as initAction>
        ${compHelper.printStatement(initAction)}
      </#list>

      ${resultClass} newResult = new ${resultClass}();
      <#list comp.getAllOutgoingPorts() as port>
        newResult.set${port.getName()?cap_first}(${port.getName()});
      </#list>
      return newResult;
    <#else>
      // state has no init action
      return ${resultParam};
    </#if>
  }
</#macro>

<#macro printTransitionFromStateMethod comp compHelper automatonHelper identifier state>
  <#assign compTypeParams><@Utils.printFormalTypeParameters comp=comp/></#assign>
  <#assign resultClass>${comp.getName()}Result${compTypeParams}</#assign>
  <#assign inputClass>${comp.getName()}Input${compTypeParams}</#assign>
  <#assign inputParam = identifier.getInputName()>
  <#assign targetEntryActionVar = "targetStateEntryAction">
  protected ${resultClass} <@transitionFromMethodName state=state/>(${inputClass} ${inputParam}) {

  ${resultClass} ${identifier.getResultName()} = <@exitMethodName state=state/>(${inputParam});

  // working copies of inputs - required to easily use the transition action block statements
    <@printLocalVariablesFromInput comp=comp compHelper=compHelper identifier=identifier/>

  // working copies of current outputs - required to easily use the transition action block statements
    <@printLocalVariablesFromResult comp=comp compHelper=compHelper identifier=identifier/>

  java.util.function.BiFunction<${inputClass}, ${resultClass}, ${resultClass}> ${targetEntryActionVar};

    <#list automatonHelper.getAllTransitionsWithGuardFrom(state) as guardedTransition>
      //transition with guard
        <#assign guardExpr = guardedTransition.getSCTBody().getPre()>
      if(
        <#list compHelper.getNamesInExpression(guardExpr) as readField>
            ${readField.name} != null &&
        </#list>
      ( ${compHelper.printExpression(guardExpr)} )
      )
      {
      // exit parent state(s)
      <#list automatonHelper.getLeavingParentStatesFromWith(state, guardedTransition) as leavingParentState>
      result.merge(<@exitMethodName state=leavingParentState/>(${identifier.getInputName()}));
      </#list>
      <#if automatonHelper.hasLeavingParentStatesFromWith(state, guardedTransition)>
      // print new working copies of current outputs
        <#list comp.getAllOutgoingPorts() as port>
          ${port.getName()} = ${resultParam}.get${port.getName()?cap_first}();
        </#list>
      </#if>


      // reaction
        <#if guardedTransition.getSCTBody().isPresentTransitionAction()
        && guardedTransition.getSCTBody().getTransitionAction().isPresentMCBlockStatement()>
            ${compHelper.printStatement(guardedTransition.getSCTBody().getTransitionAction().getMCBlockStatement())}
        </#if>

      // get entry action(s) for target state(s) and enter new state
      ${targetEntryActionVar} = (l__input, l__result) -> {
        <#list automatonHelper.getEnteringParentStatesFromWith(state, guardedTransition) as enteringParentState>
          l__result.merge(<@entryMethodName state=enteringParentState/>(l__input, l__result));
        </#list>
        return <@transitionToMethodNameFromTransition transition=guardedTransition/>(false).apply(l__input, l__result);
      };
      }
      else
    </#list>

    <#if automatonHelper.hasTransitionWithoutGuardFrom(state)>
      //first transition specified in the model without guard
        <#assign transition = automatonHelper.getFirstTransitionWithoutGuardFrom(state)>
      {
      // exit parent state(s)
        <#list automatonHelper.getLeavingParentStatesFromWith(state, transition) as leavingParentState>
          result.merge(<@exitMethodName state=leavingParentState/>(${identifier.getInputName()}));
        </#list>
        <#if automatonHelper.hasLeavingParentStatesFromWith(state, transition)>
      // print new working copies of current outputs
        <#list comp.getAllOutgoingPorts() as port>
          ${port.getName()} = ${resultParam}.get${port.getName()?cap_first}();
        </#list>
        </#if>

      // reaction
        <#if transition.getSCTBody().isPresentTransitionAction()
        && transition.getSCTBody().getTransitionAction().isPresentMCBlockStatement()>
            ${compHelper.printStatement(transition.getSCTBody().getTransitionAction().getMCBlockStatement())}
        </#if>

      // get entry action(s) for target state(s) and enter new state
      ${targetEntryActionVar} = (l__input, l__result) -> {
        <#list automatonHelper.getEnteringParentStatesFromWith(state, transition) as enteringParentState>
        l__result.merge(<@entryMethodName state=enteringParentState/>(l__input, l__result));
        </#list>
        return <@transitionToMethodNameFromTransition transition=transition/>(false).apply(l__input, l__result);
      };
      }
    <#elseif automatonHelper.hasSuperState(state) && automatonHelper.isFinalState(state)>
      // Transition from super state
      {
        ${targetEntryActionVar} = (targetStateEntryActionInput, targetStateEntryActionOutput) ->
          targetStateEntryActionOutput.merge(<@transitionFromMethodName state=automatonHelper.getSuperState(state)/>(targetStateEntryActionInput));
      }
    <#else>
      {
        ${targetEntryActionVar} = (targetStateEntryActionInput, targetStateEntryActionOutput) -> new ${resultClass}();
      }
    </#if>

    <#list comp.getAllOutgoingPorts() as port>
      ${identifier.getResultName()}.set${port.getName()?cap_first}(${port.getName()});
    </#list>

    return ${targetEntryActionVar}.apply(${identifier.getInputName()}, ${identifier.getResultName()});
  }
</#macro>

<#macro printTransitionToStateMethod comp compHelper automatonHelper identifier state>
  <#assign compTypeParams><@Utils.printFormalTypeParameters comp=comp/></#assign>
  <#assign resultClass>${comp.getName()}Result${compTypeParams}</#assign>
  <#assign inputClass>${comp.getName()}Input${compTypeParams}</#assign>
  <#assign inputParam = identifier.getInputName()>
  <#assign targetEntryActionVar = "targetStateEntryAction">
  protected java.util.function.BiFunction<${inputClass}, ${resultClass}, ${resultClass}> <@transitionToMethodName state=state/>(boolean runInitial) {
    // enter new state
    ${identifier.currentStateName} = ${comp.getName()}State.${state.getName()};
    <#if automatonHelper.hasSubStates(state)>
      // Enter sub state
      java.util.function.BiFunction<${inputClass}, ${resultClass}, ${resultClass}> ${targetEntryActionVar} =
        <@transitionToMethodName state=automatonHelper.getInitialSubStatesStream(state).findFirst().get()/>(true);
      // Return combined entry action
      return (targetStateEntryActionInput, targetStateEntryActionOutput) -> {
        // If asked for, run initial action
        ${resultClass} res = targetStateEntryActionOutput;
        if (runInitial) {
          res = <@initMethodName state=state/>(targetStateEntryActionInput, res);
        }
        // First own entry action
        res = <@entryMethodNameFromString stateName=state.getName()/>(targetStateEntryActionInput, res);
        // Then sub state with own result
        return ${targetEntryActionVar}.apply(targetStateEntryActionInput, res);
      };
    <#else>
      // Return entry action
      return (targetStateEntryActionInput, targetStateEntryActionOutput) -> {
        // If asked for, run initial action
        ${resultClass} res = targetStateEntryActionOutput;
        if (runInitial) {
          res = <@initMethodName state=state/>(targetStateEntryActionInput, res);
        }
        return <@entryMethodNameFromString stateName=state.getName()/>(targetStateEntryActionInput, res);
      };
    </#if>
  }
</#macro>

<#macro printAllStateMethods comp compHelper automatonHelper identifier state>
  <@printStateEntryMethod comp=comp compHelper=compHelper automatonHelper=automatonHelper identifier=identifier state=state/>

  <@printStateExitMethod comp=comp compHelper=compHelper automatonHelper=automatonHelper identifier=identifier state=state/>

  <@printStateInitMethod comp=comp compHelper=compHelper automatonHelper=automatonHelper identifier=identifier state=state/>

  <@printTransitionFromStateMethod comp=comp compHelper=compHelper automatonHelper=automatonHelper identifier=identifier state=state/>

  <@printTransitionToStateMethod comp=comp compHelper=compHelper automatonHelper=automatonHelper identifier=identifier state=state/>
</#macro>