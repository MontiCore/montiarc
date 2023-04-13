/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Atomic component with an input and output port. Its behavior is defined
 * through an automaton. The inverter inverts the received boolean value.
 */
component Inverter {

  port <<sync>> in Boolean i;
  port <<sync>> out Boolean o;

  /**
   * The automaton inverts messages.
   */
  automaton {
    // initial state to delay initial output
    initial state S;

    // emit received message
    S -> S [ i == true ] / { o = false; };
    S -> S [ i == false ] / { o = true; };
  }
}
