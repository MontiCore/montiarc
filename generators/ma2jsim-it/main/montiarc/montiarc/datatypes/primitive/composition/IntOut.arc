/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component IntOut {
  port sync out int p;

  automaton {
    initial state S;
    S -> S / { p = 1; };
  }
}
