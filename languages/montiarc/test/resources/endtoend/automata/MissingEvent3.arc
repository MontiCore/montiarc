/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The msg event 'o' is missing (the event symbol cannot be resolved).
 *
 * Outgoing ports are not resolvable as event symbol.
 */
component MissingEvent3 {

  port in int i;
  port out int o;

  automaton {
    initial state S;
    S -> S o;
  }
}
