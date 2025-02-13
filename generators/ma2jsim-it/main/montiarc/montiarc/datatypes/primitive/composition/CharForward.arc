/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component CharForward {
  port sync in char pIn,
       sync out char pOut;

  automaton {
    initial state S;
    S -> S / {
      char intermediate = pIn;
      pOut = intermediate;
    };
  }
}
