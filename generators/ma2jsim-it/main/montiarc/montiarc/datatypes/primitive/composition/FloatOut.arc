/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component FloatOut {
  port out float p;

  <<sync>> automaton {
    initial state S;
    S -> S / { p = 0.1f; };
  }
}
