/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import de.se_rwth.commons.logging.Log;
import montiarc.rte.behavior.AbstractBehavior;
import montiarc.rte.logging.Aspects;

/**
 * @param <C> the type of the owning component
 * @param <I> the Sync message type of the behavior.
 *           Its objects conclude the values of the input ports at the time of a tick.
 *           This type must also be bound for event behaviors as part of the 150% modeling that
 *           the generator currently implements in order to model variability.
 */
public abstract class Automaton<C, I> extends AbstractBehavior<C, I> {

  protected State state;

  protected String name;

  public Automaton(C context, State initial, String name) {
    super(context);
    this.state = initial;
    this.name = name;
  }

  /**
   * @return the current state of the state machine
   */
  public State getState() {
    return this.state;
  }

  /**
   * @return the name of this state machine
   */
  public String getName() {
    return this.name;
  }

  /**
   * Update the current state. To be used exclusively by transitions.
   *
   * @param state the new state
   */
  protected void setState(State state) {
    Log.info(state.name(), this.getName() + "#" + Aspects.ENTER_STATE);

    this.state = state;
  }
}
