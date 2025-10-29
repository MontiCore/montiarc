/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component LongForward {
  port sync in long pIn,
       sync out long pOut;

  automaton {
    initial state S;
    S -> S / {
      long intermediate = pIn;
      pOut = intermediate;
    }
  }
}
