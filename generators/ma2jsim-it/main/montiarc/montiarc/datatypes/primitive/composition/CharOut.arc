/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component CharOut {
  port out char p;

  <<sync>> automaton {
    initial state S;
    S -> S / { p = 'a'; };
  }
}
