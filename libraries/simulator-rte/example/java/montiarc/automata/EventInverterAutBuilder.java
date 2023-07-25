/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.automaton.EventAutomatonBuilder;
import montiarc.rte.automaton.State;

import java.util.List;

public class EventInverterAutBuilder extends EventAutomatonBuilder<EventInverterContext, EventInverterAut> {
  
  public EventInverterAutBuilder(EventInverterContext context, List<State> states) {
    super(context, states);
  }
  
  public EventInverterAutBuilder(EventInverterContext context) {
    super(context);
  }
  
  public EventInverterAutBuilder() {
    super();
  }
  
  @Override
  public EventAutomatonBuilder<EventInverterContext, EventInverterAut> addDefaultStates() {
    this.addStates(List.of(InverterStates.state_S));
    return this;
  }
  
  @Override
  public EventAutomatonBuilder<EventInverterContext, EventInverterAut> setDefaultInitial() {
    this.setInitial(InverterStates.state_S);
    return this;
  }
  
  @Override
  protected EventInverterAut buildActual(EventInverterContext context,
                                         List<State> states,
                                         State initial) {
    
    return new EventInverterAut(context, states, initial);
  }
  
  @Override
  public boolean isValid() {
    return this.getContext() != null
      && this.getStates() != null
      && this.getInitial() != null
      && this.getStates().contains(this.getInitial());
  }
}
