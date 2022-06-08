/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton;

/**
 * Valid model.
 */
component ValidAutomaton2 {

  port in Integer integer;

  automaton {
    initial S1;
    state S1, S2;
    S1 -> S2 [integer != null];
  }
}
