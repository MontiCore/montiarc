/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component LongForward {
  port in long pIn,
       out long pOut;

  <<sync>> automaton {
    initial state S;
    S -> S / {
      long intermediate = pIn;
      pOut = intermediate;
    };
  }
}
