/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component IntIn {
  port sync in int p;

  automaton {
    initial state S;
    S -> S / { int foo = p; }
  }
}
