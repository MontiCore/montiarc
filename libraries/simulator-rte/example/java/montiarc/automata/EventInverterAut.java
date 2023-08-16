/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.automaton.*;

import java.util.List;

public class EventInverterAut extends EventAutomaton<EventInverterContext> {

  protected EventInverterAut(EventInverterContext context,
                             List<State> states,
                             State initial) {
    super(context, states, initial);
  }
  
  public void msg_bIn() {
    if(transition_msg_bIn_1.isEnabled(state)) {
      transition_msg_bIn_1.execute(this);
    } else if(transition_noStimulus_1.isEnabled(state)) {
      transition_noStimulus_1.execute(this); // TODO discuss: should this be executed here? If no, where else?
    }
  }
  
  public void msg_iIn() {
    if(transition_msg_iIn_1.isEnabled(state)) {
      transition_msg_iIn_1.execute(this);
    } else if(transition_noStimulus_1.isEnabled(state)) {
      transition_noStimulus_1.execute(this); // TODO discuss: should this be executed here? If no, where else?
    }
  }
  
  @Override
  public void tick() {
    if(transition_tick_1.isEnabled(state)) {
      transition_tick_1.execute(this);
    } else if(transition_noStimulus_1.isEnabled(state)) {
      transition_noStimulus_1.execute(this); // TODO discuss: should this be executed here? If no, where else?
    }
  }
  
  protected Transition transition_msg_bIn_1 = new TransitionBuilder()
      .setSource(EventInverterStates.state_S)
      .setTarget(EventInverterStates.state_S)
      .setGuard(() -> true)
      .setAction(() -> {
        java.lang.System.out.println("Message stimulus bIn.");
        context.port_bOut().send(!context.port_bIn().pollBuffer().getData()); // simplified because this is not the focus of this example
      })
      .build();
  
  protected Transition transition_msg_iIn_1 = new TransitionBuilder()
      .setSource(EventInverterStates.state_S)
      .setTarget(EventInverterStates.state_S)
      .setGuard(() -> true)
      .setAction(() -> {
        java.lang.System.out.println("Message stimulus iIn.");
        context.port_iOut().send(-context.port_iIn().pollBuffer().getData()); // simplified because this is not the focus of this example
      })
      .build();
  
  protected Transition transition_tick_1 = new TransitionBuilder()
      .setSource(EventInverterStates.state_S)
      .setTarget(EventInverterStates.state_S)
      .setGuard(() -> true)
      .setAction(() -> {
        java.lang.System.out.println("Tick stimulus.");
      })
      .build();
  
  protected Transition transition_noStimulus_1 = new TransitionBuilder()
      .setSource(EventInverterStates.state_S)
      .setTarget(EventInverterStates.state_S)
      .setGuard(() -> true)
      .setAction(() -> {
        java.lang.System.out.println("Stimulus-free transition.");
      })
      .build();
}
