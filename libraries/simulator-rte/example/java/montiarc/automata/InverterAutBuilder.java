/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.automaton.*;

import java.util.List;

public class InverterAutBuilder extends SyncAutomatonBuilder<InverterContext, InverterAut> {
  
  public InverterAutBuilder(InverterContext context, List<State> states, List<Transition> trans) {
    super(context, states, trans);
  }
  
  public InverterAutBuilder(InverterContext context) {
    super(context);
  }
  
  public InverterAutBuilder() {
    super();
  }
  
  @Override
  public SyncAutomatonBuilder<InverterContext, InverterAut> addDefaultStates() {
    this.addStates(List.of(InverterStates.state_S));
    return this;
  }
  
  @Override
  public SyncAutomatonBuilder<InverterContext, InverterAut> addDefaultTransitions() {
    this.addTransition(new TransitionBuilder()
        .setSource(InverterStates.state_S)
        .setTarget(InverterStates.state_S)
        .setGuard(() -> this.getContext().port_i().peekBuffer().getData())
        .setAction(() -> {
          this.getContext().port_i().pollBuffer(); // remove "used" messsage
          this.getContext().port_o().send(false);
        })
        .build());
    
    this.addTransition(new TransitionBuilder()
        .setSource(InverterStates.state_S)
        .setTarget(InverterStates.state_S)
        .setGuard(() -> !this.getContext().port_i().peekBuffer().getData())
        .setAction(() -> {
          this.getContext().port_i().pollBuffer(); // remove "used" messsage
          this.getContext().port_o().send(true);
        })
        .build());
    
    return this;
  }
  
  @Override
  public SyncAutomatonBuilder<InverterContext, InverterAut> setDefaultInitial() {
    this.setInitial(InverterStates.state_S);
    return this;
  }
  
  @Override
  protected InverterAut buildActual(InverterContext context,
                                    List<State> states,
                                    List<Transition> trans,
                                    State initial) {
    
    return new InverterAut(context, states, trans, initial);
  }
}
