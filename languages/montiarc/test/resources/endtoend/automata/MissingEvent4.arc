/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The msg events 'p' and 'v' are missing (the event symbols
 * cannot be resolved).
 *
 * Component parameters and fields are not resolvable as event symbols.
 */
component MissingEvent4 {

  int v = 0;

  automaton {
    initial state S;
    S -> S p;
    S -> S v;
  }
}
