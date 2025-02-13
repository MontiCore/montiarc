/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ShortIn {
  port sync in short p;

  automaton {
    initial state S;
    S -> S / { short foo = p; };
  }
}
