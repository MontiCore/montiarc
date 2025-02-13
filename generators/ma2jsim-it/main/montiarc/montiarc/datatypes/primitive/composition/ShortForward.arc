/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ShortForward {
  port sync in short pIn,
       sync out short pOut;

  automaton {
    initial state S;
    S -> S / {
      short intermediate = pIn;
      pOut = intermediate;
    };
  }
}
