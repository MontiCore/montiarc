/* (c) https://github.com/MontiCore/monticore */
package variables;

component VFullyQualified(Types.Direction p) {

  port out Types.Direction o;

  Types.Direction f = Types.Direction.FORWARDS;

  <<sync>> automaton {
    initial state S;

    S -> S / {
      Types.Direction v = f;
      o = v;
    };
  }
}
