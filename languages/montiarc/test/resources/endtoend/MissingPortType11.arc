/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing (simple name) of outgoing port o is missing
 * (the datatype cannot be resolved). The value of port o is assigned inside
 * in an assignment expression.
 */
component MissingPortType11 {

  port in int i;
  port out Missing o;

  automaton {
    initial state S;
    S -> S i / { o = i; }
  }

}
