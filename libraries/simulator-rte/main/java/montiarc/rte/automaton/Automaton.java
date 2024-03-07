/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import montiarc.rte.behavior.AbstractBehavior;

import java.util.Collection;

public abstract class Automaton<C> extends AbstractBehavior<C> {

  protected State state;
  protected Collection<State> states;

  public Automaton(C context, Collection<State> states, State initial) {
    super(context);
    this.state = initial;
    this.states = states;
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
  public State getState() {
    return this.state;
  }

  /**
   * Update the current state. To be used exclusively by transitions.
   *
   * @param state the new state
   */
  protected void setState(State state) {
    this.state = state;
  }
}
