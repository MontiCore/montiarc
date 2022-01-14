<#-- (c) https://github.com/MontiCore/monticore -->
<#-- This template creates automaton implementations for components that have automatons. -->

<#import "/templates/util/Utils.ftl" as Utils>

<#macro printImplementationClassMethods comp compHelper identifier isTOPClass=false>
  <#assign compName = comp.getName()>
  <#assign automaton = compHelper.getAutomatonBehavior().get()>
  <#assign automatonHelper = compHelper.automatonHelperFrom(automaton)>

  // component variables
  <@Utils.printVariables comp=comp/>

  // component config parameters
  <@Utils.printConfigParameters comp=comp/>

  // current state
  <@Utils.printMember visibility="private" type=compName+"State" name=identifier.getCurrentStateName()/>

  <@printAutomatonStateEnum comp=comp compHelper=compHelper automatonHelper=automatonHelper/>

  <@printConstructor comp=comp isTOPClass=isTOPClass/>

  <@printGetInitialValues comp=comp compHelper=compHelper identifier=identifier/>

  <@printCompute comp=comp compHelper=compHelper automatonHelper=automatonHelper identifier=identifier/>
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
      <#assign paramName=param.getName()>
      this.${paramName} = ${paramName};
    </#list>
  }
</#macro>

<#macro printAutomatonStateEnum comp compHelper automatonHelper>
  <#assign compName=comp.getName()>
  <#assign automaton = compHelper.getAutomatonBehavior().get()>
  private static enum ${compName}State {
    <#list automatonHelper.getAutomatonStates() as state>
      ${state.getName()} <#sep> , </#sep>
    </#list>
  }
</#macro>

<#macro printGetInitialValues comp compHelper identifier>
  <#assign compName=comp.getName()>
  <#assign compTypeParams> <@Utils.printFormalTypeParameters comp=comp/> </#assign>
  <#assign resultVarName = identifier.getResultName()>
  <#assign automaton = compHelper.getAutomatonBehavior().get()>
  <#assign optInitialState = automaton.streamInitialStates().findFirst()>
  <#assign optInitialOutputDecl = automaton.streamInitialOutput().findFirst()>

  @Override
  public ${compName}Result ${compTypeParams} getInitialValues() {

    // init initial result
    final ${compName}Result ${compTypeParams} ${resultVarName} = new ${compName}Result ${compTypeParams} ();
    // Create local fields that record initial values for output ports.
    <#list comp.allOutgoingPorts as outPort>
      <#assign portType = compHelper.getRealPortTypeString(comp, outPort)>
      ${portType} ${outPort.getName()} = null;
    </#list>

    <#if optInitialOutputDecl.isPresent()>
      <#assign initialReaction = automatonHelper.scABodyToTransitionAction(optInitialOutputDecl.get().getSCABody()).getMCBlockStatement()>
      // initial reaction
      ${compHelper.printStatement(initialReaction)}
    </#if>

    ${identifier.getCurrentStateName()} = ${compName}State.${optInitialState.get().getName()};

    // transfer locally recorded initial port values into the result
    <#list comp.allOutgoingPorts as outPort>
      <#assign portSetterName = "set" + outPort.getName()?cap_first>
      ${resultVarName}.${portSetterName}( ${outPort.getName()} );
    </#list>

    return ${resultVarName};
  }
</#macro>

<#macro printCompute comp compHelper automatonHelper identifier>
  <#assign compName=comp.getName()>
  <#assign compTypeParams> <@Utils.printFormalTypeParameters comp=comp/> </#assign>
  <#assign inputVarName = identifier.getInputName()>
  <#assign resultVarName = identifier.getResultName()>
  <#assign automaton = compHelper.getAutomatonBehavior().get()>

  @Override
  public ${compName}Result ${compTypeParams} compute(${compName}Input ${compTypeParams} ${inputVarName}) {
    // fetch current values of input ports
    <#list comp.allIncomingPorts as inPort>
      <#assign portType = compHelper.getRealPortTypeString(comp, inPort)>
      final ${portType} ${inPort.getName()} = ${inputVarName}.get${inPort.name?cap_first}();
    </#list>

    // init result
    final ${compName}Result ${compTypeParams} ${resultVarName} = new ${compName}Result ${compTypeParams} ();
    // Create local fields that record values for output ports.
    <#list comp.allOutgoingPorts as outPort>
      <#assign portType = compHelper.getRealPortTypeString(comp, outPort)>
      ${portType} ${outPort.getName()} = null;
    </#list>

    <#-- generate implementation of the automaton: -->
    switch (${identifier.getCurrentStateName()}) {
      <#list automatonHelper.getAutomatonStates() as state>

        case ${state.name}:
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
            // reaction
              <#if guardedTransition.getSCTBody().isPresentTransitionAction()
              && guardedTransition.getSCTBody().getTransitionAction().isPresentMCBlockStatement()>
                  ${compHelper.printStatement(guardedTransition.getSCTBody().getTransitionAction().getMCBlockStatement())}
              </#if>

            // enter new state
              ${identifier.currentStateName} = ${compName}State.${guardedTransition.targetName};
            }
          </#list>
          <#if automatonHelper.hasTransitionWithoutGuardFrom(state)>
              //first transition specified in the model without guard
          <#assign transition = automatonHelper.getFirstTransitionWithoutGuardFrom(state)>
              <#if transition.getSCTBody().isPresentPre()>
                  <#assign guardExpr = transition.getSCTBody().getPre()>
                if(
                  <#list compHelper.getNamesInExpression(guardExpr) as readField>
                      ${readField.name} != null &&
                  </#list>
                ( ${compHelper.printExpression(guardExpr)} )
                )
              </#if>
            {
            // reaction
              <#if transition.getSCTBody().isPresentTransitionAction()
              && transition.getSCTBody().getTransitionAction().isPresentMCBlockStatement()>
                  ${compHelper.printStatement(transition.getSCTBody().getTransitionAction().getMCBlockStatement())}
              </#if>

            // enter new state
              ${identifier.currentStateName} = ${compName}State.${transition.targetName};
            }
          </#if>
          break;
      </#list>
    }

    // transfer locally recorded port values into the result
    <#list comp.allOutgoingPorts as outPort>
      ${resultVarName}.set${outPort.name?cap_first}( ${outPort.name} );
    </#list>

    return ${resultVarName};
  }
</#macro>