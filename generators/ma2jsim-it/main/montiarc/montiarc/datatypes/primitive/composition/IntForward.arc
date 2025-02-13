/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component IntForward {
  port sync in int pIn,
       sync out int pOut;

  automaton {
    initial state S;
    S -> S / {
      int intermediate = pIn;
      pOut = intermediate;
    };
  }
}
