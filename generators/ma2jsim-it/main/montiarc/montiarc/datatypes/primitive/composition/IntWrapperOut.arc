/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component IntWrapperOut {
  port sync out Integer p;

  automaton {
    initial state S;
    S -> S / { p = 1; }
  }
}
