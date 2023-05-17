<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state", "automaton", "input", "output", "result", "counter")}

<#if ast.getSCTBody().isPresentPre()>
protected void from${state.getName()}To${ast.targetName}${counter}() {
  montiarc.rte.log.Log.trace("Transition from${state.getName()}To${ast.targetName}${counter}");
<#else>
protected void from${state.getName()}To${ast.targetName}NoGuard${counter}() {
  montiarc.rte.log.Log.trace("Transition from${state.getName()}To${ast.targetName}NoGuard${counter}");
</#if>

  // Context for Solver
  Context ctx = montiarc.rte.dse.TestController.getCtx();

  //inputs
  ${input}

  // exit state(s)
  this.exit(this.get${identifier.getCurrentStateName()?cap_first}(), States.${state.getName()});
  <#list autHelper.getLeavingParentStatesFromWith(automaton, state, ast) as state>
    exit${state.getName()}();
  </#list>
  // output
  ${output}
  // reaction
  <#if ast.getSCTBody().isPresentTransitionAction() && ast.getSCTBody().getTransitionAction().isPresentMCBlockStatement()>
    ${compHelperDse.printStatement(ast.getSCTBody().getTransitionAction().getMCBlockStatement())}
  </#if>
  // result
  ${result}
  // entry state(s)
  <#list autHelper.getEnteringParentStatesFromWith(automaton, state, ast) as state>
    entry${state.getName()}();
  </#list>
  this.transitionTo${ast.targetName}();
}