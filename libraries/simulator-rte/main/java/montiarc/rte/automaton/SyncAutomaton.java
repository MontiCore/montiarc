/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @param <C> the type of the owning component
 * @param <I> the Sync message type of the behavior.
 *           Its objects conclude the values of the input ports at the time of a tick.
 *           This type must also be bound for event behaviors as part of the 150% modeling that
 *           the generator currently implements in order to model variability.
 */
public abstract class SyncAutomaton<C, I> extends Automaton<C, I> {

  protected Collection<Transition<I>> transitions;

  protected SyncAutomaton(C context,
                          State initial) {
    super(context, initial);
    transitions = new ArrayList<>();
  }

  /**
   * @return the transitions of the state machine
   */
  protected Collection<Transition<I>> getTransitions() {
    return this.transitions;
  }

  protected Collection<Transition<I>> getValidTransitions(I syncedInputs) {
    return this.transitions.stream()
      .filter(tr -> tr.isEnabled(getState(), syncedInputs))
      .collect(Collectors.toList());
  }
  
  @Override
  public void tick(I syncedInputs) {
    getValidTransitions(syncedInputs).stream().findFirst().ifPresent(tr -> tr.execute(this, syncedInputs));
  }
}
