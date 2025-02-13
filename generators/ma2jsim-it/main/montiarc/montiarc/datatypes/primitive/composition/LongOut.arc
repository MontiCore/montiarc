/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component LongOut {
  port sync out long p;

  automaton {
    initial state S;
    S -> S / { p = 1L; };
  }
}
