/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component CharWrapperIn {
  port sync in Character p;

  automaton {
    initial state S;
    S -> S / { char foo = p; }
  }
}
