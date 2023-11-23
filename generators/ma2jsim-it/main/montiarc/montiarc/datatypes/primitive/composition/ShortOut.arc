/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ShortOut {
  port out short p;

  <<sync>> automaton {
    initial state S;
    S -> S / { int foo = 1; };
  }
}
