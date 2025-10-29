/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component BooleanOut {
  port sync out boolean p;

  automaton {
    initial state S;
    S -> S / { p = true; }
  }
}
