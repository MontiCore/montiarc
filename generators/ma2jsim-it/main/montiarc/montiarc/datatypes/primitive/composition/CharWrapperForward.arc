/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component CharWrapperForward {
  port sync in Character pIn,
       sync out Character pOut;

  automaton {
    initial state S;
    S -> S / {
      char intermediate = pIn;
      pOut = intermediate;
    };
  }
}
