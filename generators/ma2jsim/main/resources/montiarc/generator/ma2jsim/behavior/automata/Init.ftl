<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("automaton")}
@Override
public void init() {
  <#-- First die initial action, then a delay -->
  state.initWithSub();

  <#-- Only after the delay, execute automaton stuff: entry to the the initial state -->
  state.enterWithSub();
  setState(state.getInitialSubstate());
}
