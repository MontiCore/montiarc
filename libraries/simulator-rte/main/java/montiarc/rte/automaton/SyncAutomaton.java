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

  protected Collection<Transition> getValidTransitions() {
    return this.transitions.stream().filter(tr -> tr.isEnabled(getState())).collect(Collectors.toList());
  }
  
  @Override
  public void tick() {
    getValidTransitions().stream().findFirst().ifPresentOrElse(
      tr -> tr.execute(this),
      this::executeDefaultAction
    );
  }

  /** The action executed if there is no valid transition to take */
  protected abstract void executeDefaultAction();
}
