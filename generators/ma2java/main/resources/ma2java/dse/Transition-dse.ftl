<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state", "automaton", "output", "result")}

<#if ast.getSCTBody().isPresentPre()>
if(${compHelper.printExpression(ast.getSCTBody().getPre())}) {
</#if>
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
<#if ast.getSCTBody().isPresentPre()>
}
</#if>