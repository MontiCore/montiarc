/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The msg events 'o', 'v', 'p', 'T', and 'S' are missing
 * (the event symbols cannot be resolved).
 *
 * Outgoing ports, component fields, parameters, type-parameters, and states
 * are not resolvable as event symbols.
 */
component MissingEventInTransition5<T>(int p) {
  port in int i;
  port out int o;

  int v = 0;

  automaton {
    initial state S;
    S -> S o;
    S -> S v;
    S -> S p;
    S -> S T;
    S -> S S;
  }
}
