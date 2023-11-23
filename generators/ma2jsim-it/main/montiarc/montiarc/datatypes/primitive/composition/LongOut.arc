/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component LongOut {
  port out long p;

  <<sync>> automaton {
    initial state S;
    S -> S / { p = 1L; };
  }
}
