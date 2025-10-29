/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component FloatOut {
  port sync out float p;

  automaton {
    initial state S;
    S -> S / { p = 0.1f; }
  }
}
