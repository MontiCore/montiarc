/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ShortIn {
  port in short p;

  <<sync>> automaton {
    initial state S;
    S -> S / { short foo = p; };
  }
}
