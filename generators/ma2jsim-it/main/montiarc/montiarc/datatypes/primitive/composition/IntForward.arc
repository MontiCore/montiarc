/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component IntForward {
  port in int pIn,
       out int pOut;

  <<sync>> automaton {
    initial state S;
    S -> S / {
      int intermediate = pIn;
      pOut = intermediate;
    };
  }
}
