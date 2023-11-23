/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ShortForward {
  port in short pIn,
       out short pOut;

  <<sync>> automaton {
    initial state S;
    S -> S / {
      short intermediate = pIn;
      pOut = intermediate;
    };
  }
}
