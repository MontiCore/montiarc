/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component IntWrapperIn {
  port sync in Integer p;

  automaton {
    initial state S;
    S -> S / { int foo = p; }
  }
}
