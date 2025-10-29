/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component LongWrapperOut {
  port sync out Long p;

  automaton {
    initial state S;
    S -> S / { p = 1L; }
  }
}
