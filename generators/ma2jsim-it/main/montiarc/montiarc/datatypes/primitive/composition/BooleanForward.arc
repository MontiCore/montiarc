/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component BooleanForward {
  port in boolean pIn,
       out boolean pOut;

  <<sync>> automaton {
    initial state S;
    S -> S / {
      boolean intermediate = pIn;
      pOut = intermediate;
    };
  }
}
