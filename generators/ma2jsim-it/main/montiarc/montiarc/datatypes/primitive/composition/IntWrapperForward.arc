/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component IntWrapperForward {
  port sync in Integer pIn,
       sync out Integer pOut;

  automaton {
    initial state S;
    S -> S / {
      int intermediate = pIn;
      pOut = intermediate;
    }
  }
}
