/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component BooleanForward {
  port sync in boolean pIn,
       sync out boolean pOut;

  automaton {
    initial state S;
    S -> S / {
      boolean intermediate = pIn;
      pOut = intermediate;
    }
  }
}
