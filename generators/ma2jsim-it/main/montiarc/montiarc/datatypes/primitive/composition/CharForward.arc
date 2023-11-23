/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component CharForward {
  port in char pIn,
       out char pOut;

  <<sync>> automaton {
    initial state S;
    S -> S / {
      char intermediate = pIn;
      pOut = intermediate;
    };
  }
}
