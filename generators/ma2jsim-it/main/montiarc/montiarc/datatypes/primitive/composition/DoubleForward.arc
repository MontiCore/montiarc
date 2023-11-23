/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component DoubleForward {
  port in double pIn,
       out double pOut;

  <<sync>> automaton {
    initial state S;
    S -> S / {
      double intermediate = pIn;
      pOut = intermediate;
    };
  }
}
