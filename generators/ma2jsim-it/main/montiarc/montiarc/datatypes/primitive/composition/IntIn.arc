/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component IntIn {
  port in int p;

  <<sync>> automaton {
    initial state S;
    S -> S / { int foo = p; };
  }
}
