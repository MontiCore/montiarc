/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component DoubleWrapperForward {
  port sync in Double pIn,
       sync out Double pOut;

  automaton {
    initial state S;
    S -> S / {
      double intermediate = pIn;
      pOut = intermediate;
    }
  }
}
