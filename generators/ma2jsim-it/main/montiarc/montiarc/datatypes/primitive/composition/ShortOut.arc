/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ShortOut {
  port sync out short p;

  automaton {
    initial state S;
    S -> S / { int foo = 1; };
  }
}
