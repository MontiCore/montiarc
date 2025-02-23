/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component BooleanWrapperForward {
  port sync in Boolean pIn,
       sync out Boolean pOut;

  automaton {
    initial state S;
    S -> S / {
      boolean intermediate = pIn;
      pOut = intermediate;
    };
  }
}
