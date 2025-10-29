/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component FloatWrapperIn {
  port sync in Float p;

  automaton {
    initial state S;
    S -> S / { float foo = p; }
  }
}
