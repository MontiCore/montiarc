/* (c) https://github.com/MontiCore/monticore */
package variables;

component VFullyQualified(Types.Direction p) {

  port sync out Types.Direction o;

  Types.Direction f = Types.Direction.FORWARDS;

  automaton {
    initial state S;

    S -> S / {
      Types.Direction v = f;
      o = v;
    }
  }
}
