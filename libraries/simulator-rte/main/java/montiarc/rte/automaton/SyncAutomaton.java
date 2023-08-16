/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import java.util.Collection;
import java.util.stream.Collectors;

public abstract class SyncAutomaton<Context> extends Automaton<Context> {

  protected Collection<Transition> transitions;

  public SyncAutomaton(Context context,
                       Collection<State> states,
                       Collection<Transition> transitions,
                       State initial) {
    super(context, states, initial);
    this.transitions = transitions;
  }

  /**
   * @return the transitions of the state machine
   */
  protected Collection<Transition> getTransitions() {
    return this.transitions;
  }

  /**
   * Check whether there is a transition from the current state whose guard condition is fulfilled.
   * This method should operate on the automaton owner.
   *
   * @return true if there is a transition that can currently be executed
   */
  public boolean canExecuteTransition() {
    return !getValidTransitions().isEmpty();
  }

  /**
   * Executes a transition which is currently valid.
   * This method should operate on the automaton owner.
   */
  public void executeAnyValidTransition() {
    getValidTransitions().stream().findFirst().ifPresent(tr -> tr.execute(this));
  }

  protected Collection<Transition> getValidTransitions() {
    return this.transitions.stream().filter(tr -> tr.isEnabled(getState())).collect(Collectors.toList());
  }
}
