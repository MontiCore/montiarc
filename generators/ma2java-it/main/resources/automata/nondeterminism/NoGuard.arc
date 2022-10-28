/* (c) https://github.com/MontiCore/monticore */
package automata.nondeterminism;

/**
 * Atomic component without ports. Features an automaton with ambiguous state
 * transitions.
 */
component NoGuard {

  /**
   * Automaton with ambiguous transitions.
   */
  automaton {
    initial state A;
    state B;
    state C;
    state D;

    // ambiguous, transition to B or C
    A -> B;
    A -> C;

    // ambiguous, transition to A or C
    B -> C;
    B -> A;

    // ambiguous, transition to A, B or D
    C -> A;
    C -> B;
    C -> D;
  }
}
