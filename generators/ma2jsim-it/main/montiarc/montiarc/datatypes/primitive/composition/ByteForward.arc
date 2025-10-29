/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ByteForward {
  port sync in byte pIn,
       sync out byte pOut;

  automaton {
    initial state S;
    S -> S / {
      byte intermediate = pIn;
      pOut = intermediate;
    }
  }
}
