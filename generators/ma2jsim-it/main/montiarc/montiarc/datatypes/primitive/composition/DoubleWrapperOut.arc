/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component DoubleWrapperOut {
  port sync out Double p;

  automaton {
    initial state S;
    S -> S / { p = 0.1; };
  }
}
