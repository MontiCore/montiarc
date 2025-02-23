/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ShortWrapperOut {
  port sync out Short p;

  automaton {
    initial state S;
    S -> S / { int foo = 1; };
  }
}
