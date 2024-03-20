/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. The type of the incoming and the outgoing port cannot
 * be resolved. The ports are used as part of an expression statement of a
 * transition action. The tool should report an error for the missing type but
 * no type mismatch error, as source and target are of the same (missing) type.
 */
component MissingPortType16 {

  port in Missing i;
  port out Missing o;

  automaton {
    initial state S;
    S -> S i / { o = i; };
  }

}
