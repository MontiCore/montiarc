/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing (simple name) of incoming port i is missing
 * (the datatype cannot be resolved). The value of port i is accessed inside
 * a guard.
 */
component MissingPortType9 {

  port in Missing i;
  port out int o;

  automaton {
    initial state S;
    S -> S [i] i;
  }

}
