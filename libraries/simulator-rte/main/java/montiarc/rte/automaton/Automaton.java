/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import java.util.Collection;

public abstract class Automaton<Context> {

  protected Context context;
  protected State state;
  protected Collection<State> states;

  public Automaton(Context context, Collection<State> states, State initial) {
    this.context = context;
    this.state = initial;
    this.states = states;
  }
  
  /**
   * @return the context (i.e., owning component) of the state machine
   */
  protected Context getContext() {
    return this.context;
  }

  /**
   * @return the state space of the state machine
   */
  protected Collection<State> getStates() {
    return this.states;
  }

  /**
   * @return the current state of the state machine
   */
  protected State getState() {
    return this.state;
  }
}
