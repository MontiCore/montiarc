/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import montiarc.rte.automaton.transition.Transition;

import java.util.Collection;

public abstract class Automaton {

  protected Collection<Transition> transitions;

  protected State currentState;

  public Collection<Transition> getTransitions() {
    return transitions;
  }

  public State getCurrentState() {
    return currentState;
  }

  public Automaton(Collection<Transition> transitions, State currentState) {
    this.transitions = transitions;
    this.currentState = currentState;
  }

  /**
   * Check whether there is a transition from the current state whose guard condition is fulfilled.
   * This method should operate on the automaton owner.
   *
   * @return true if there is a transition that can currently be executed
   */
  public abstract boolean canExecuteTransition();

  /**
   * Executes a transition which is currently valid.
   * This method should operate on the automaton owner.
   */
  public abstract void executeAnyValidTransition();
}
