/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import montiarc.rte.behavior.AbstractBehavior;

public abstract class Automaton<C> extends AbstractBehavior<C> {

  protected State state;

  public Automaton(C context, State initial) {
    super(context);
    this.state = initial;
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
