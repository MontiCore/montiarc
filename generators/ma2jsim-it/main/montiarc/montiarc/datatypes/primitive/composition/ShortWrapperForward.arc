/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ShortWrapperForward {
  port sync in Short pIn,
       sync out Short pOut;

  automaton {
    initial state S;
    S -> S / {
      short intermediate = pIn;
      pOut = intermediate;
    }
  }
}
