/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. The type of the outgoing port cannot be resolved.
 * The port is used as part of an expression statement of a transition action.
 * The tool should report an error for the missing type and an error for the
 * mismatching types.
 */
component MissingPortType15 {

  port in int i;
  port out Missing o;

  automaton {
    initial state S;
    S -> S i / { o = i; };
  }

}
