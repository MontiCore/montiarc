/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component IntOut {
  port out int p;

  <<sync>> automaton {
    initial state S;
    S -> S / { p = 1; };
  }
}
