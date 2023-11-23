/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component FloatForward {
  port in float pIn,
       out float pOut;

  <<sync>> automaton {
    initial state S;
    S -> S / {
      float intermediate = pIn;
      pOut = intermediate;
    };
  }
}
