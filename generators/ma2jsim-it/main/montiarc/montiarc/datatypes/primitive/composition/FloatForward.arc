/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component FloatForward {
  port sync in float pIn,
       sync out float pOut;

  automaton {
    initial state S;
    S -> S / {
      float intermediate = pIn;
      pOut = intermediate;
    };
  }
}
