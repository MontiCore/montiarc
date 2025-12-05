/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: The type Missing (simple name) of ports i and o is missing
 * (the datatype cannot be resolved). Port o is assigned the value of port i
 * in an assignment expression.
 */
component MissingPortType12 {

  port in Missing i;
  port out Missing o;

  automaton {
    initial state S;
    S -> S i / { o = i; }
  }

}
