/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component DoubleForward {
  port sync in double pIn,
       sync out double pOut;

  automaton {
    initial state S;
    S -> S / {
      double intermediate = pIn;
      pOut = intermediate;
    };
  }
}
