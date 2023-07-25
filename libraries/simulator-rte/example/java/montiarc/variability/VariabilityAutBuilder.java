/* (c) https://github.com/MontiCore/monticore */
package montiarc.variability;

import montiarc.rte.automaton.*;

import java.util.List;

public class VariabilityAutBuilder extends SyncAutomatonBuilder<VariabilityContext, VariabilityAut> {
  
  public VariabilityAutBuilder(VariabilityContext context, List<State> states, List<Transition> trans) {
    super(context, states, trans);
  }
  
  public VariabilityAutBuilder(VariabilityContext context) {
    super(context);
  }
  
  public VariabilityAutBuilder() {
    super();
  }
  
  @Override
  public SyncAutomatonBuilder<VariabilityContext, VariabilityAut> addDefaultStates() {
    this.addStates(List.of(VariabilityStates.state_SomeState));
    return this;
  }
  
  @Override
  public SyncAutomatonBuilder<VariabilityContext, VariabilityAut> addDefaultTransitions() {
    this.addTransition(new TransitionBuilder()
        .setSource(VariabilityStates.state_SomeState)
        .setTarget(VariabilityStates.state_SomeState)
        .setGuard(() -> true)
        .setAction(() -> {})
        .build());
    return this;
  }
  
  @Override
  public SyncAutomatonBuilder<VariabilityContext, VariabilityAut> setDefaultInitial() {
    this.setInitial(VariabilityStates.state_SomeState);
    return this;
  }
  
  @Override
  protected VariabilityAut buildActual(VariabilityContext context,
                                       List<State> states,
                                       List<Transition> transitions,
                                       State initial) {
    
    return new VariabilityAut(context, states, transitions, initial);
  }
}
