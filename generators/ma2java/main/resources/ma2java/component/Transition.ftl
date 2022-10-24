<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("currentState", "automaton", "output", "result")}

<#if ast.getSCTBody().isPresentPre()>
if(${compHelper.printExpression(ast.getSCTBody().getPre())}) {
</#if>
// exit state(s)
this.exit${currentState.getName()}();
<#list autHelper.getLeavingParentStatesFromWith(automaton, currentState, ast) as state>
  exit${state.getName()}();
</#list>
// output
${output}
// reaction
<#if ast.getSCTBody().isPresentTransitionAction() && ast.getSCTBody().getTransitionAction().isPresentMCBlockStatement()>
  ${compHelper.printStatement(ast.getSCTBody().getTransitionAction().getMCBlockStatement())}
</#if>
// result
${result}
// entry state(s)
<#list autHelper.getEnteringParentStatesFromWith(automaton, currentState, ast) as state>
  entry${state.getName()}();
</#list>
this.transitionTo${ast.targetName}();
<#if ast.getSCTBody().isPresentPre()>
}
</#if>