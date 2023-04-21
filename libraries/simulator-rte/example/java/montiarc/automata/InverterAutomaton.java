/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.automaton.Automaton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class InverterAutomaton extends Automaton {

  protected Inverter owner;

  public InverterAutomaton(Inverter owner) {
    super(new ArrayList<>(), InverterState.S);
    this.owner = owner;
    this.currentState = InverterState.S;
    transitions.add(
      new InverterTransition(
        InverterState.S, // source state
        InverterState.S, // target state
        () -> owner.i.peekBuffer().getData() == true, // guard
        () -> owner.o.send(false), // transition action
        List.of(owner.i) // list of all used ports (to drop inputs)
      )
    );
    transitions.add(
      new InverterTransition(
        InverterState.S, // source state
        InverterState.S, // target state
        () -> owner.i.peekBuffer().getData() == false, // guard
        () -> owner.o.send(true), // transition action
        List.of(owner.i) // list of all used ports (to drop inputs)
      )
    );
  }

  /**
   * Check whether there is a transition from the current state whose guard condition is fulfilled.
   * This method should operate on the automaton owner.
   *
   * @return true if there is a transition that can currently be executed
   */
  @Override
  public boolean canExecuteTransition() {
    return transitions.stream().anyMatch(transition ->
      transition instanceof InverterTransition
        && transition.getSourceState() == this.getCurrentState()
        && ((InverterTransition) transition).getGuard().check());
  }

  /**
   * Executes a transition which is currently valid.
   * This method should operate on the automaton owner.
   */
  @Override
  public void executeAnyValidTransition() {
    getAllValidTransitions().stream()
      .findFirst()
      .ifPresent(transition -> transition.performTransition(owner));
  }

  protected List<InverterTransition> getAllValidTransitions() {
    return transitions.stream()
      .filter(transition -> transition instanceof InverterTransition)
      .map(transition -> (InverterTransition) transition)
      .filter(transition -> transition.getSourceState() == this.currentState)
      .filter(transition -> transition.getGuard().check())
      .collect(Collectors.toList());
  }
}
