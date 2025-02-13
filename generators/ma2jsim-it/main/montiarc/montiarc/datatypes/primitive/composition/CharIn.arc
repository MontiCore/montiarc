/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component CharIn {
  port sync in char p;

  automaton {
    initial state S;
    S -> S / { char foo = p; };
  }
}
