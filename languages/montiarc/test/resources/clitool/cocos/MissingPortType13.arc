/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. The type of the incoming port cannot be resolved.
 * The port is used as variable inside a transition guard. The tool should
 * report an error for the missing type and an error for the non-boolean guard.
 */
component MissingPortType13 {

  port in Missing i;
  port out int o;

  automaton {
    initial state S;
    S -> S [i] i;
  }

}
