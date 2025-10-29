/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component LongWrapperForward {
  port sync in Long pIn,
       sync out Long pOut;

  automaton {
    initial state S;
    S -> S / {
      long intermediate = pIn;
      pOut = intermediate;
    }
  }
}
