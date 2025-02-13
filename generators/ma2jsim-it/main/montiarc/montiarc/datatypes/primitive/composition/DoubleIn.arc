/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component DoubleIn {
  port sync in double p;

  automaton {
    initial state S;
    S -> S / { double foo = p; };
  }
}
