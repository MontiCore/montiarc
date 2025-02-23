/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component FloatWrapperForward {
  port sync in Float pIn,
       sync out Float pOut;

  automaton {
    initial state S;
    S -> S / {
      float intermediate = pIn;
      pOut = intermediate;
    };
  }
}
