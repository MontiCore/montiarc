/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ByteForward {
  port in byte pIn,
       out byte pOut;

  <<sync>> automaton {
    initial state S;
    S -> S / {
      byte intermediate = pIn;
      pOut = intermediate;
    };
  }
}
