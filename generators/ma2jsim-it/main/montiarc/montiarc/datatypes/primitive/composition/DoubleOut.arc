/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component DoubleOut {
  port sync out double p;

  automaton {
    initial state S;
    S -> S / { p = 0.1; };
  }
}
