/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Invalid model: The automaton declares multiple states with the same name.
 */
component DuplicateStates1 {

  automaton {
    initial state s1;

    state s2;
    state s2;

    s1 -> s2;
    s2 -> s1;
  }
}
