/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component FloatWrapperOut {
  port sync out Float p;

  automaton {
    initial state S;
    S -> S / { p = 0.1f; };
  }
}
