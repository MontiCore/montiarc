/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component CharOut {
  port sync out char p;

  automaton {
    initial state S;
    S -> S / { p = 'a'; };
  }
}
