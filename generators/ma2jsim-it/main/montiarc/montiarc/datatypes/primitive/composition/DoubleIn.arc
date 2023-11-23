/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component DoubleIn {
  port in double p;

  <<sync>> automaton {
    initial state S;
    S -> S / { double foo = p; };
  }
}
