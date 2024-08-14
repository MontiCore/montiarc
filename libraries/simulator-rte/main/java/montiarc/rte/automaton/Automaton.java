/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import montiarc.rte.behavior.AbstractBehavior;

/**
 * @param <C> the type of the owning component
 * @param <I> the Sync message type of the behavior.
 *           Its objects conclude the values of the input ports at the time of a tick.
 *           This type must also be bound for event behaviors as part of the 150% modeling that
 *           the generator currently implements in order to model variability.
 */
public abstract class Automaton<C, I> extends AbstractBehavior<C, I> {

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
