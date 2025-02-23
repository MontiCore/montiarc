/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ByteWrapperForward {
  port sync in Byte pIn,
       out Byte pOut;

  automaton {
    initial state S;
    S -> S / {
      byte intermediate = pIn;
      pOut = intermediate;
    };
  }
}
