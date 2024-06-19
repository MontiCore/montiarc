<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("automaton")}
@Override
public void init() {
  state.initWithSub();
  setState(state.getInitialSubstate());
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SendInitialTicksOnDelayedPorts.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
}
